package me.sxl.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sxl.common.utils.RedisUtils;
import me.sxl.gateway.model.*;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Configuration
@Component
@Slf4j
public class DubboReferenceConfig {

    @Value("${dubbo.reference.registry.protocol}")
    private String protocol;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${dubbo.reference.loadBalance}")
    private String loadBalance;

    private RedisUtils redisUtils;

    private ApplicationConfig applicationConfig = new ApplicationConfig();

    private RegistryCenterConfig registryCenterConfig;

    @Autowired
    public void setRegistryCenterConfig(RegistryCenterConfig registryCenterConfig) {
        this.registryCenterConfig = registryCenterConfig;
    }

    @Autowired
    public void setRedisUtils(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    /**
     * 将所有接口信息初始化在内存中,并暴露get方法获取
     */
    private static Map<DubboReferenceKey, DubboReferenceValue> config = new HashMap<>();

    @PostConstruct
    public void init() {
        applicationConfig.setName(applicationName);

        for (Address addr : this.registryCenterConfig.getAddrs()) {
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAddress(addr.getAddress());
            registryConfig.setProtocol(protocol);
            applicationConfig.setRegistry(registryConfig);
        }

        Set<Object> apiSet = this.redisUtils.sGet(Constants.DUBBO_REDIS_KEY);

        ObjectMapper objectMapper = new ObjectMapper();
        for (Object o : apiSet) {
            DubboReferenceModel model = objectMapper.convertValue(o, DubboReferenceModel.class);
            config.put(DubboReferenceKey
                            .builder()
                            .reqUri(model.getRequestUri())
                            .reqMethod(model.getRequestMethod())
                            .version(model.getVersion())
                            .build(),
                    DubboReferenceValue
                            .builder()
                            .reference(buildReference(model))
                            .model(model)
                            .build());
        }

        log.info("[Gateway] Load Dubbo APIs From Redis: {}", config);
    }

    /**
     * 对外暴露的get方法,获取reference和model
     *
     * @param key 获取reference和model的唯一key
     * @return DubboReferenceValue
     */
    public DubboReferenceValue get(DubboReferenceKey key) {
        return config.get(key);
    }

    /**
     * 若请求到gateway的dubbo接口不在内存中,则去数据库中查询,若结果集不为空,加入到缓存中
     *
     * @param key 获取reference和model的唯一key
     * @return 获取到持久化信息则加入到内存并返回, 未获取到则返回null
     */
    public DubboReferenceValue putIfAbsent(DubboReferenceKey key) {
        // 获取最新的缓存加载结果
        Set<Object> dubboSet = this.redisUtils.sGet(Constants.DUBBO_REDIS_KEY);
        ObjectMapper objectMapper = new ObjectMapper();
        for (Object o : dubboSet) {
            DubboReferenceModel model = objectMapper.convertValue(o, DubboReferenceModel.class);
            if (model.getRequestUri().equals(key.getReqUri()) && model.getRequestMethod().equals(key.getReqMethod()) && model.getVersion().equals(key.getVersion())) {
                config.put(key,
                        DubboReferenceValue
                                .builder()
                                .reference(buildReference(model))
                                .model(model)
                                .build());
                return this.get(key);
            }
        }
        return null;
    }

    private ReferenceConfig<GenericService> buildReference(DubboReferenceModel model) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface(model.getInterfaceClass());
        reference.setTimeout(model.getTimeout());
        reference.setGeneric(true);
        reference.setApplication(applicationConfig);
        reference.setValidation("Validated");
        reference.setLoadbalance(StringUtils.isEmpty(loadBalance) ? "random" : loadBalance);
        return reference;
    }

}
