package me.sxl.gateway.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author yyconstantine
 */
@Data
@Builder
public class DubboReferenceModel {

    /**
     * reqMethod + reqUri取hashcode,保证同个请求的接口唯一性
     */
    private Integer id;

    /**
     * 请求uri
     */
    private String requestUri;

    /**
     * 请求方法,GET/POST/PUT/DELETE
     */
    private String requestMethod;

    /**
     * dubbo接口全路径
     */
    private String dubboInterface;

    /**
     * 超时时间,可为空
     */
    private Integer timeout;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 具体方法,拿uri置换
     */
    private String dubboMethod;

    /**
     * 接口签名全路径
     */
    private String methodSign;

    /**
     * 接口版本
     */
    private String version;

}
