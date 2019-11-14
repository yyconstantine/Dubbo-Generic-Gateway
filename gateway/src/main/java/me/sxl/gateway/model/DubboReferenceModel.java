package me.sxl.gateway.model;

import lombok.Data;

@Data
public class DubboReferenceModel {

    /**
     * 主键
     */
    private Long id;

    /**
     * dubbo接口全路径
     */
    private String interfaceClass;

    /**
     * dubbo接口具体方法名称
     */
    private String interfaceMethod;

    /**
     * dubbo接口方法签名全路径
     */
    private String interfaceMethodSign;

    /**
     * 接口超时时间
     */
    private Integer timeout;

    /**
     * 重试次数
     */
    private Integer retries;

    /**
     * 定义的接口版本号
     */
    private String version;

    /**
     * 请求方法,GET/POST/PUT/DELETE
     */
    private String requestMethod;

    /**
     * 请求路径,走统一网关但是需要对外提供具体访问的接口路径,这里定义为REST风格
     */
    private String requestUri;

    /**
     * 接口状态,0可用1不可用
     */
    private Integer status;

    /**
     * 接口归属人
     */
    private String owner;

}
