package me.sxl.gateway.config;

import lombok.Data;
import me.sxl.gateway.model.Address;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author songxianglong
 * @date 2019/12/12 20:05
 */
@Component
@Data
@ConfigurationProperties(prefix = "dubbo.reference.registry.addrs")
public class RegistryCenterConfig {

    private List<Address> addrs = new ArrayList<>();

}
