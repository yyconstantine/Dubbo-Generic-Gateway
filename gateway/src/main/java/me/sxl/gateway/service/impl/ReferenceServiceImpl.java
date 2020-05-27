package me.sxl.gateway.service.impl;

import me.sxl.gateway.config.DubboReferenceConfig;
import me.sxl.gateway.model.DubboReferenceKey;
import me.sxl.gateway.model.DubboReferenceValue;
import me.sxl.gateway.service.ReferenceService;
import me.sxl.gateway.util.ResponseUtils;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class ReferenceServiceImpl implements ReferenceService {

    private DubboReferenceConfig dubboReferenceConfig;

    @Autowired
    public void setDubboReferenceConfig(DubboReferenceConfig dubboReferenceConfig) {
        this.dubboReferenceConfig = dubboReferenceConfig;
    }

    @Override
    public Optional<DubboReferenceValue> findDubboReferenceBy(DubboReferenceKey key) throws IOException {
        DubboReferenceValue config = dubboReferenceConfig.get(key);
        if (config == null) {
            config = dubboReferenceConfig.putIfAbsent(key);
            if (config == null) {
                return Optional.empty();
            }
        }
        return Optional.of(config);
    }

    @Override
    public String invoke(Map<String, Object> params, DubboReferenceValue reference) throws IOException {
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference.getReference());

        String rpcResult;
        if (StringUtils.isEmpty(reference.getModel().getMethodSign())) {
            // 接口签名无信息
            rpcResult =
                    ResponseUtils.writeObjectValue2JsonString(genericService.$invoke(reference.getModel().getDubboMethod(),
                            null, null));
        } else if (reference.getModel().getMethodSign().startsWith("java")) {
            Object[] objects = new Object[1];
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                objects[0] = entry.getValue();
            }

            // 接口签名为基本数据类型或集合类型
            rpcResult =
                    ResponseUtils.writeObjectValue2JsonString(genericService.$invoke(reference.getModel().getDubboMethod(),
                            new String[]{reference.getModel().getMethodSign()},
                            objects));

        } else {
            // 接口签名为pojo类型
            String[] parameterTypes = new String[]{reference.getModel().getMethodSign()};
            Map<String, Object> pojo = new HashMap<>(8);
            params.put("class", reference.getModel().getMethodSign());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                pojo.put(entry.getKey(), entry.getValue());
            }
            rpcResult =
                    ResponseUtils.writeObjectValue2JsonString(genericService.$invoke(reference.getModel().getDubboMethod(),
                            parameterTypes,
                            new Object[]{pojo}));

        }
        return rpcResult;
    }
}
