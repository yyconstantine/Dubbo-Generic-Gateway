package me.sxl.gateway.model;

import lombok.Data;

@Data
public class ApiGatewayDTO {

    /**
     * 客户端随机生成的密钥,通过rsa公钥加密
     */
    private String key;

    /**
     * 客户端上传的真正数据,json格式，通过客户端生成的随机密钥对称加密
     */
    private String value;

}
