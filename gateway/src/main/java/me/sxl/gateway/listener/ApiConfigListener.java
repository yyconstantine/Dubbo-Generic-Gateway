package me.sxl.gateway.listener;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.reference.ReferenceConfigHandler;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author yyconstantine
 * @date 2020/5/14 上午 11:33
 */
@Slf4j
@Configuration
public class ApiConfigListener {

    @NacosInjected
    private ConfigService configService;

    private ReferenceConfigHandler handler;

    @Resource
    public void setHandler(ReferenceConfigHandler handler) {
        this.handler = handler;
    }

    @NacosConfigListener(dataId = "${gateway.nacos-data:testData}", groupId = "${gateway.nacos-group:testGroup}")
    public void onMessage(String config) {
        log.info("API config changed: {}", config);
        // 刷新local cache
        this.handler.refresh();
    }

}
