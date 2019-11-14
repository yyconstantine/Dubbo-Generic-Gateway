package me.sxl.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sxl.common.constant.ErrorEnum;
import me.sxl.common.constant.OkEnum;
import me.sxl.common.model.ResponseEntity;
import me.sxl.common.utils.DESUtil;
import me.sxl.gateway.config.DubboReferenceConfig;
import me.sxl.gateway.model.ApiGatewayDTO;
import me.sxl.gateway.model.DubboReferenceKey;
import me.sxl.gateway.model.DubboReferenceValue;
import me.sxl.gateway.util.RequestDecodeUtil;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
public class ApiGatewayController {

    @Value("${rsa.key.private}")
    private String privateKey;

    @PostMapping("/route/{method}/{uri}")
    @SuppressWarnings("unchecked")
    public ResponseEntity route(@PathVariable String method,
                                @PathVariable String uri,
                                @RequestBody ApiGatewayDTO gatewayDTO) {
        log.info("上传参数: method >> {}, uri >> {}, DTO >> {}", method, uri, gatewayDTO);
        // 这里做解密过程
        String[] paramsAndDesKey = RequestDecodeUtil.decode2ParamsAndDesKey(gatewayDTO, privateKey);
        String reqParams = paramsAndDesKey[0];
        String desKey = paramsAndDesKey[1];

        if (StringUtils.isEmpty(reqParams) || StringUtils.isEmpty(desKey)) {
            log.error("参数解析错误");
            return ResponseEntity.error(ErrorEnum.REQ_DECODE_ERROR);
        }

        log.info("参数解析结果为: {}", reqParams);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        try {
            map = mapper.readValue(reqParams, Map.class);
        } catch (JsonProcessingException e) {
            log.error("参数序列化解析出错: ", e);
        }

        DubboReferenceValue config = DubboReferenceConfig.get(DubboReferenceKey
                                                        .builder()
                                                        .reqMethod(method)
                                                        .reqUri(uri)
                                                        .build());

        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(config.getReference());

        Object result;

        if (StringUtils.isEmpty(config.getModel().getInterfaceMethodSign())) {
            // 当不设置方法签名时,默认其只有一个String类型的参数
            Object[] params = new Object[1];
            for (String key : map.keySet()) {
                params[0] = map.get(key);
            }

            result = genericService.$invoke(config.getModel().getInterfaceClass(),
                                            new String[] {params[0].getClass().getName()},
                                            params);
            log.info("Single parameter invoke return: {}", result);
        } else {
            // 遵循阿里开发规范,当两个参数及以上时封装为pojo,pojo全路径记录在数据库中
            String[] parameterTypes = new String[] {config.getModel().getInterfaceMethodSign()};
            Map<String, Object> params = new HashMap<>();
            params.put("class", config.getModel().getInterfaceMethodSign());
            for (String key : map.keySet()) {
                params.put(key, map.get(key));
            }

            result = genericService.$invoke(config.getModel().getInterfaceClass(),
                                            parameterTypes,
                                            new Object[] {params});
            log.info("Multi parameters invoke return: {}", result);
        }

        String resultByDes = DESUtil.encrypt(result.toString(), desKey);

        return ResponseEntity.ok(OkEnum.GLOBAL_SEARCH_OK, resultByDes);
    }

}
