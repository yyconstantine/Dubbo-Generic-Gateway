package me.sxl.gateway.registry;

import org.springframework.stereotype.Service;

/**
 * @author yyconstantine
 * @date 2020/5/27 上午 9:43
 */
@Service
public class ZookeeperApiRegistryStrategy implements ApiRegistryStrategy {
    @Override
    public void registry(Object bean, String beanName) {}
}
