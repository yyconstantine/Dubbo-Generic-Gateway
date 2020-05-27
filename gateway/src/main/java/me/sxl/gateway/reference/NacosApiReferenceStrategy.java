package me.sxl.gateway.reference;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.config.PropertyConfig;
import me.sxl.gateway.model.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NacosApiReferenceStrategy extends ReferenceConfigHandler implements ApiReferenceStrategy {

    @NacosInjected
    private ConfigService configService;

    private PropertyConfig propertyConfig;

    @Autowired
    public void setPropertyConfig(PropertyConfig propertyConfig) {
        this.propertyConfig = propertyConfig;
    }

    @Override
    public String reference() {
        try {
            return this.configService.getConfig(this.propertyConfig.getNacosDataId(), this.propertyConfig.getNacosGroupId(), Constants.DEFAULT_NACOS_TIMEOUT);
        } catch (NacosException e) {
            log.warn("[Gateway] An error occurred when get config from nacos.");
            e.printStackTrace();
        }
        return null;
    }
}
