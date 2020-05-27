package me.sxl.gateway.config;

import lombok.Getter;
import me.sxl.gateway.model.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置信息集中管理类
 *
 * @author yyconstantine
 * @date 2020/5/15 上午 11:47
 */
@Configuration
@EnableConfigurationProperties({RegistryCenterConfig.class})
@Getter
public class PropertyConfig {

    @Value("${spring.application.name:api-gateway}")
    private String applicationName;

    @Value("${gateway.registry.protocol:dubbo}")
    private String protocol;

    @Value("${gateway.loadbalance:random}")
    private String loadBalance;

    @Value("${gateway.scan-package}")
    private String scanPackages;

    @Value("${gateway.nacos-group:testGroup}")
    private String nacosGroupId;

    @Value("${gateway.nacos-data:testData}")
    private String nacosDataId;

    private RegistryCenterConfig registryCenterConfig;

    @Autowired
    public void setRegistryCenterConfig(RegistryCenterConfig registryCenterConfig) {
        this.registryCenterConfig = registryCenterConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public Properties properties() {
        return Properties.builder()
                .applicationName(applicationName)
                .protocol(protocol)
                .loadBalance(loadBalance)
                .addresses(registryCenterConfig.getAddrs())
                .build();
    }

}
