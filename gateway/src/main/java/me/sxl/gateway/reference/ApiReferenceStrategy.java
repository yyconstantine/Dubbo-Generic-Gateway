package me.sxl.gateway.reference;

import org.apache.dubbo.common.extension.SPI;

/**
 * 接口引用策略
 */
@SPI
public interface ApiReferenceStrategy {

    /**
     * api接口发现
     * 并根据注册进行进行服务路由
     * 其中,nacos/zookeeper支持接口动态变更(redis暂时没有监听回调)
     */
    String reference();

}
