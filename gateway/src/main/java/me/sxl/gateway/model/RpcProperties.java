package me.sxl.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * rpc服务属性,暂时只支持dubbo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcProperties {

    private String applicationName;

    private String protocol;

    private String loadBalance;

    private List<Address> addresses;

}
