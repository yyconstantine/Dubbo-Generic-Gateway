package me.sxl.gateway.config;

import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.model.DubboReferenceKey;
import me.sxl.gateway.model.DubboReferenceModel;
import me.sxl.gateway.model.DubboReferenceValue;
import me.sxl.gateway.service.ReferenceService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
@Component
@Slf4j
public class DubboReferenceConfig {

    @Value("${registry.address}")
    private String registryAddr;

    @Value("${spring.application.name}")
    private String applicationName;

    private ApplicationConfig applicationConfig;

    private RegistryConfig registryConfig;

    private ReferenceService referenceService;

    @Autowired
    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    /**
     * 将所有接口信息初始化在内存中,并暴露get方法获取
     */
    private static Map<DubboReferenceKey, DubboReferenceValue> config = new HashMap<>();

    @PostConstruct
    public void init() {
        List<DubboReferenceModel> referenceList = referenceService.listReference();
        log.info("初始化加载dubbo泛化接口: {}", referenceList);

        applicationConfig = new ApplicationConfig();
        applicationConfig.setName(applicationName);

        registryConfig = new RegistryConfig();
        registryConfig.setAddress(registryAddr);

        applicationConfig.setRegistry(registryConfig);

        referenceList.forEach(model -> {
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            reference.setApplication(applicationConfig);
            reference.setRegistry(registryConfig);
            reference.setInterface(model.getInterfaceClass());
            reference.setVersion(model.getVersion());
            reference.setRetries(model.getRetries());
            reference.setGeneric(true);

            config.put(
                    DubboReferenceKey
                    .builder()
                    .reqUri(model.getRequestUri())
                    .reqMethod(model.getRequestMethod())
                    .build(),
                    DubboReferenceValue
                            .builder()
                            .model(model)
                            .reference(reference)
                            .build()
            );
        });

        log.info("初始化加载完成,加载后的参数为: {}", config);
    }

    /**
     * 对外暴露的get方法,获取reference和model
     * @param key 获取reference和model的唯一key
     * @return DubboReferenceValue
     */
    public DubboReferenceValue get(DubboReferenceKey key) {
        return config.get(key);
    }

    /**
     * 若请求到gateway的dubbo接口不在内存中,则去数据库中查询,若结果集不为空,加入到缓存中
     * @param key 获取reference和model的唯一key
     * @return 获取到持久化信息则加入到内存并返回,未获取到则返回null
     */
    public DubboReferenceValue putIfAbsent(DubboReferenceKey key) {
        Optional<DubboReferenceModel> modelOpt = this.referenceService.getByKey(key);
        if (modelOpt.isPresent()) {
            ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
            reference.setApplication(applicationConfig);
            reference.setRegistry(registryConfig);
            reference.setInterface(modelOpt.get().getInterfaceClass());
            reference.setVersion(modelOpt.get().getVersion());
            reference.setRetries(modelOpt.get().getRetries());
            reference.setGeneric(true);

            config.put(key,
                    DubboReferenceValue.builder()
                        .model(modelOpt.get())
                        .reference(reference)
                        .build());
            return this.get(key);
        }
        return null;
    }

}
