package me.sxl.gateway.service.impl;

import me.sxl.gateway.config.DubboReferenceConfig;
import me.sxl.gateway.model.DubboReferenceKey;
import me.sxl.gateway.model.DubboReferenceValue;
import me.sxl.gateway.service.ReferenceService;
import me.sxl.gateway.util.ResponseUtil;
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
    public Optional<DubboReferenceValue> findDubboReferenceBy(DubboReferenceKey key) {
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

        String rpcResult = "";
        if (StringUtils.isEmpty(reference.getModel().getInterfaceMethodSign())) {
            // 接口签名无信息
            rpcResult =
                    ResponseUtil.writeObjectValue2JsonString(genericService.$invoke(reference.getModel().getInterfaceMethod(),
                            null, null));
        } else if (reference.getModel().getInterfaceMethodSign().startsWith("java")) {
            Object[] objects = new Object[1];
            for (String key : params.keySet()) {
                objects[0] = params.get(key);
            }

            // 接口签名为基本数据类型或集合类型
            rpcResult =
                    ResponseUtil.writeObjectValue2JsonString(genericService.$invoke(reference.getModel().getInterfaceMethod(),
                            new String[]{reference.getModel().getInterfaceMethodSign()},
                            objects));

        } else {
            // 接口签名为pojo类型
            String[] parameterTypes = new String[]{reference.getModel().getInterfaceMethodSign()};
            Map<String, Object> pojo = new HashMap<>(8);
            params.put("class", reference.getModel().getInterfaceMethodSign());
            for (String key : params.keySet()) {
                pojo.put(key, params.get(key));
            }
            rpcResult =
                    ResponseUtil.writeObjectValue2JsonString(genericService.$invoke(reference.getModel().getInterfaceMethod(),
                            parameterTypes,
                            new Object[]{pojo}));

        }
        return rpcResult;
    }
}
