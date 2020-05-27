package me.sxl.gateway.config;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.model.*;
import me.sxl.gateway.model.Properties;
import me.sxl.gateway.model.constant.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Configuration
@Component
@Slf4j
public class DubboReferenceConfig {

    private static Map<DubboReferenceKey, DubboReferenceValue> configMap = new HashMap<>();

    private Properties properties;

    private PropertyConfig propertyConfig;

    @NacosInjected
    private ConfigService configService;

    @Autowired
    public void setPropertyConfig(PropertyConfig propertyConfig) {
        this.propertyConfig = propertyConfig;
    }

    @Autowired
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    private ApplicationConfig application = new ApplicationConfig();

    /**
     * 这里首先在服务启动时进行加载,先注册基本的本地配置信息(application、registry等)
     * 再将各服务上报的接口信息缓存至本地
     */
    @PostConstruct
    public void init() throws IOException, NacosException {
        // 普通编码配置方式
        application.setName(this.properties.getApplicationName());

        // 连接注册中心配置
        for (Address addr : this.properties.getAddresses()) {
            RegistryConfig registry = new RegistryConfig();
            registry.setAddress(addr.getAddress());
            registry.setProtocol(this.properties.getProtocol());
            application.setRegistry(registry);
        }

        // 获取全量数据
        this.putAll();

        log.debug("[Gateway] read apis from Nacos: {}", configMap.keySet());
    }

    /**
     * 获取请求接口对应的dubbo服务实例
     *
     * @param key uri + method
     * @return dubbo服务实例/null
     */
    public DubboReferenceValue get(DubboReferenceKey key) {
        return configMap.get(key);
    }

    /**
     * 当本地缓存不存在请求接口对应信息,则通过该方法请求配置中心redis进行添加
     * 若存在相同key属性的dubbo接口信息则添加到内存中,不存在则返回null,对应返回404
     *
     * @param key uri + method
     * @return dubbo服务实例
     */
    public DubboReferenceValue putIfAbsent(DubboReferenceKey key) throws IOException {
        // 获取最新的缓存加载结果
        String config;
        try {
            config = configService.getConfig(this.propertyConfig.getNacosDataId(), this.propertyConfig.getNacosGroupId(), Constants.DEFAULT_NACOS_TIMEOUT);
        } catch (NacosException e) {
            log.warn("[Gateway] An error occurred when get config from Nacos: ", e);
            return null;
        }
        Map<Object, Object> apis = convert(config);
        if (apis == null || apis.size() == 0) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        for (Map.Entry<Object, Object> api : apis.entrySet()) {
            DubboReferenceModel model = objectMapper.convertValue(api.getValue(), DubboReferenceModel.class);
            if (model.getRequestUri().equals(key.getReqUri()) && model.getRequestMethod().equals(key.getReqMethod())) {
                configMap.put(key,
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

    private void putAll() throws NacosException, IOException {
        // 获取全量接口
        String config = configService.getConfig(this.propertyConfig.getNacosDataId(), this.propertyConfig.getNacosGroupId(), Constants.DEFAULT_NACOS_TIMEOUT);
        Map<Object, Object> apis = convert(config);
        if (apis == null || apis.size() == 0) {
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

    public void refresh() throws IOException, NacosException {
        // 清除缓存数据
        configMap.clear();
        // 获取全量数据
        this.putAll();
    }

    private Map<Object, Object> convert(String config) throws IOException {
        if (StringUtils.isEmpty(config)) {
            log.debug("[Gateway] Load empty api-config from Nacos");
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<Object, Object> apis = objectMapper.readValue(config, Map.class);
        return apis;
    }

    private ReferenceConfig<GenericService> buildReference(DubboReferenceModel model) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface(model.getDubboInterface());
        reference.setTimeout(model.getTimeout());
        reference.setGeneric(true);
        reference.setApplication(application);
        reference.setValidation("Validated");
        reference.setLoadbalance(this.properties.getLoadBalance());
        return reference;
    }

}
