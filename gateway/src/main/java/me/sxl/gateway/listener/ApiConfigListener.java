package me.sxl.gateway.listener;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.config.DubboReferenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author yyconstantine
 * @date 2020/5/14 上午 11:33
 */
@Slf4j
@Configuration
public class ApiConfigListener {

    @NacosInjected
    private ConfigService configService;

    private DubboReferenceConfig dubboReferenceConfig;

    @Autowired
    public void setDubboReferenceConfig(DubboReferenceConfig dubboReferenceConfig) {
        this.dubboReferenceConfig = dubboReferenceConfig;
    }

    @NacosConfigListener(dataId = "${dubbo.reference.nacos-data:testData}", groupId = "${dubbo.reference.nacos-group:testGroup}")
    public void onMessage(String config) throws IOException, NacosException {
        log.info("API config changed: {}", config);
        // 刷新local cache
        this.dubboReferenceConfig.refresh();
    }

}
