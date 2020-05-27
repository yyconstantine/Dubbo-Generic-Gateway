package me.sxl.gateway.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

/**
 * 接口注册协议,使用SPI方式进行加载,默认使用nacos
 *
 * @author yyconstantine
 * @date 2020/5/27 上午 9:26
 */
@SPI("${dubbo.reference.register:nacos}")
public interface ApiRegistry {

    /**
     * api注册接口,通过继承
     * @see ApiResourceLoader
     * 获取在Spring Bean后置处理器拿到的需要注册的接口
     * 通过SPI机制加载不同的配置中心进行配置发布
     * @param bean
     * @param beanName
     */
    @Adaptive
    void registry(Object bean, String beanName) throws JsonProcessingException, NacosException;

}
