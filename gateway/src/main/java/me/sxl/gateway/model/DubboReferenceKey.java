package me.sxl.gateway.model;

import lombok.Builder;
import lombok.Data;

/**
 * 用作唯一标示,置换reference和model信息
 */
@Data
@Builder
public class DubboReferenceKey {

    /**
     * 请求路径
     */
    private String reqUri;

    /**
     * 请求方法
     */
    private String reqMethod;

}
