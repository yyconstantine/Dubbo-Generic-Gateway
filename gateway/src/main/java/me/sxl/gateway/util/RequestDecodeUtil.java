package me.sxl.gateway.util;

import lombok.extern.slf4j.Slf4j;
import me.sxl.common.utils.DESUtil;
import me.sxl.common.utils.RSAUtil;
import me.sxl.gateway.model.ApiGatewayDTO;

@Slf4j
public class RequestDecodeUtil {

    public static String[] decode2ParamsAndDesKey(ApiGatewayDTO gatewayDTO, String RSAPrivateKey) {
        String desPrivateKey = getDesPrivateKey(gatewayDTO.getKey(), RSAPrivateKey);
        log.warn("Des-PrivateKey: {}", desPrivateKey);
        String params = getParameterStr(gatewayDTO.getValue(), desPrivateKey);
        return new String[] {params, desPrivateKey};
    }

    private static String getDesPrivateKey(String str, String rsaPrivateKey) {
        return RSAUtil.decryptByPrivateKey(str, rsaPrivateKey);
    }

    private static String getParameterStr(String str, String desPrivateKey) {
        return DESUtil.decrypt(str, desPrivateKey);
    }

}
