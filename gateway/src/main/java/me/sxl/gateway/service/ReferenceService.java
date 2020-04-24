package me.sxl.gateway.service;

import me.sxl.gateway.model.DubboReferenceKey;
import me.sxl.gateway.model.DubboReferenceValue;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public interface ReferenceService {
    /**
     * 根据uri && method && version 获取dubbo引用实例,即路由信息
     *
     * @param key uri && method
     * @return dubbo引用实例
     */
    Optional<DubboReferenceValue> findDubboReferenceBy(DubboReferenceKey key);

    /**
     * 真正执行rpc接口调用逻辑(rpc接口调用)的地方
     *
     * @param params    请求参数
     * @param reference dubbo服务引用实例
     * @return dubbo接口返回的json格式的数据(已经过非空处理)
     * @throws IOException IO异常
     */
    String invoke(Map<String, Object> params, DubboReferenceValue reference) throws IOException;
}
