package me.sxl.gateway.registry;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.sxl.gateway.model.constant.Constants;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author yyconstantine
 * @date 2020/5/27 上午 9:31
 */
@Service
public class NacosApiRegistryStrategy extends ApiResourceLoader implements ApiRegistryStrategy {

    @NacosInjected
    private ConfigService configService;

    @Override
    public void registry(Object bean, String beanName) throws JsonProcessingException, NacosException {
        Map<Object, Object> apis = this.getApis();

        ObjectMapper objectMapper = new ObjectMapper();
        String config = configService.getConfig(this.propertyConfig.getNacosDataId(),
                this.propertyConfig.getNacosGroupId(), Constants.DEFAULT_NACOS_TIMEOUT);
        if (!StringUtils.isEmpty(config)) {
            Map originConfig = objectMapper.readValue(config, Map.class);
            apis.putAll(originConfig);
        }
        this.configService.publishConfig(this.propertyConfig.getNacosDataId(), this.propertyConfig.getNacosGroupId(),
                objectMapper.writeValueAsString(apis));
    }
}
