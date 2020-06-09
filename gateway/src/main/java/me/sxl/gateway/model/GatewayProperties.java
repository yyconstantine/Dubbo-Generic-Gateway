package me.sxl.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关注册、引用相关属性
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayProperties {

    private String referenceProtocol;

    private String registryProtocol;

}
