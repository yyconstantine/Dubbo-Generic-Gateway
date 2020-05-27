package me.sxl.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yyconstantine
 * @date 2020/5/15 上午 11:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Properties {

    private String applicationName;

    private String protocol;

    private String loadBalance;

    private List<Address> addresses;

}
