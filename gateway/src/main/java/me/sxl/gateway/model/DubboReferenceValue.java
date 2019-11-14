package me.sxl.gateway.model;

import lombok.Builder;
import lombok.Data;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;

@Data
@Builder
public class DubboReferenceValue {

    private DubboReferenceModel model;

    private ReferenceConfig<GenericService> reference;

}
