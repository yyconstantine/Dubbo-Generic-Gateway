package me.sxl.gateway.reference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.model.*;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ReferenceConfigHandler {

    private final Map<DubboReferenceKey, DubboReferenceValue> configMap = new HashMap<>();

    private ExtensionLoader<ApiReferenceStrategy> extensionLoader;

    private Properties properties;

    private final ApplicationConfig application = new ApplicationConfig();

    @Autowired
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    private ApiReferenceStrategy getCustomExtension() {
        if (extensionLoader == null) {
            extensionLoader = ExtensionLoader.getExtensionLoader(ApiReferenceStrategy.class);
        }
        return extensionLoader.getExtension(this.properties.getGatewayProperties().getReferenceProtocol());
    }

    @PostConstruct
    public void init() {
        // 普通编码配置方式
        application.setName(this.properties.getRpcProperties().getApplicationName());

        // 连接注册中心配置
        for (Address addr : this.properties.getRpcProperties().getAddresses()) {
            RegistryConfig registry = new RegistryConfig();
            registry.setAddress(addr.getAddress());
            registry.setProtocol(this.properties.getRpcProperties().getProtocol());
            application.setRegistry(registry);
        }

        // 获取全量数据
        this.putAll();

        log.debug("[Gateway] read apis from Nacos: {}", configMap.keySet());
    }

    public DubboReferenceValue get(DubboReferenceKey key) {
        return configMap.get(key);
    }

    public DubboReferenceValue putIfAbsent(DubboReferenceKey key) {
        Map<Object, Object> apis = this.convert(this.getCustomExtension().reference());
        if (CollectionUtils.isEmpty(apis)) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        for (Map.Entry<Object, Object> api : apis.entrySet()) {
            DubboReferenceModel model = objectMapper.convertValue(api.getValue(), DubboReferenceModel.class);
            if (model.getRequestUri().equals(key.getReqUri()) && model.getRequestMethod().equals(key.getReqMethod())) {
                this.configMap.put(key,
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

    private void putAll() {
        Map<Object, Object> apis;
        apis = convert(this.getCustomExtension().reference());
        if (CollectionUtils.isEmpty(apis)) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        for (Map.Entry<Object, Object> api : apis.entrySet()) {
            DubboReferenceModel model = objectMapper.convertValue(api.getValue(), DubboReferenceModel.class);

            configMap.put(DubboReferenceKey
                            .builder()
                            .reqUri(model.getRequestUri())
                            .reqMethod(model.getRequestMethod())
                            .build(),
                    DubboReferenceValue
                            .builder()
                            .reference(buildReference(model))
                            .model(model)
                            .build());
        }
    }

    public void refresh() {
        configMap.clear();
        putAll();
    }

    @SuppressWarnings("unchecked")
    private Map<Object, Object> convert(String config) {
        if (StringUtils.isEmpty(config)) {
            log.debug("[Gateway] Load empty api-config from config-center.");
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<Object, Object> apis = null;
        try {
            apis = objectMapper.readValue(config, Map.class);
        } catch (JsonProcessingException e) {
            log.warn("[Gateway] config from config-center can't covert to Map.class.");
            e.printStackTrace();
        }
        return apis;
    }

    private ReferenceConfig<GenericService> buildReference(DubboReferenceModel model) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface(model.getDubboInterface());
        reference.setTimeout(model.getTimeout());
        reference.setGeneric(true);
        reference.setApplication(application);
        reference.setValidation("Validated");
        reference.setLoadbalance(this.properties.getRpcProperties().getLoadBalance());
        return reference;
    }

}
