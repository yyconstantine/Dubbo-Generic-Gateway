package me.sxl.gateway.config;

import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.mapper.ReferenceMapper;
import me.sxl.gateway.model.DubboReferenceKey;
import me.sxl.gateway.model.DubboReferenceModel;
import me.sxl.gateway.model.DubboReferenceValue;
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

@Configuration
@Component
@Slf4j
public class DubboReferenceConfig {

    @Value("${registry.address}")
    private String registryAddr;

    @Value("${spring.application.name}")
    private String applicationName;

    private ReferenceMapper referenceMapper;

    @Autowired
    public void setReferenceMapper(ReferenceMapper referenceMapper) {
        this.referenceMapper = referenceMapper;
    }

    /**
     * 将所有接口信息初始化在内存中,并暴露get方法获取
     */
    private static Map<DubboReferenceKey, DubboReferenceValue> config = new HashMap<>();

    @PostConstruct
    public void init() {
        List<DubboReferenceModel> referenceList = referenceMapper.dubboReferenceList();
        log.info("初始化加载dubbo泛化接口: {}", referenceList);

        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(applicationName);

        RegistryConfig registryConfig = new RegistryConfig();
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
    public static DubboReferenceValue get(DubboReferenceKey key) {
        return config.get(key);
    }

}
