package me.sxl.order.facade;

import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.annotation.DubboApi;
import me.sxl.gateway.model.ResponseEntity;
import me.sxl.gateway.model.constant.OkEnum;
import me.sxl.order.api.facade.OrderApi;
import me.sxl.order.api.model.OrderDTO;
import org.apache.dubbo.config.annotation.Service;

@Service
@Slf4j
public class OrderApiFacade implements OrderApi {

    @Override
    @DubboApi(api = "order", method = "POST", owner = "yyconstantine", timeout = 3000, version = "v2", retries = -1)
    public ResponseEntity order(OrderDTO orderDTO) {
        log.info("Order Params: {}", orderDTO.toString());
        /*if (true) {
            throw new OrderServiceRuntimeException(ErrorEnum.UNKNOWN);
        }*/
        return ResponseEntity.ok(OkEnum.GLOBAL_INSERT_OK);
    }
}
