package me.sxl.order.facade;

import lombok.extern.slf4j.Slf4j;
import me.sxl.common.constant.ErrorEnum;
import me.sxl.common.constant.OkEnum;
import me.sxl.common.model.ResponseEntity;
import me.sxl.order.api.exception.OrderServiceRuntimeException;
import me.sxl.order.api.facade.OrderApi;
import me.sxl.order.api.model.OrderDTO;
import org.apache.dubbo.config.annotation.Service;

@Service
@Slf4j
public class OrderApiFacade implements OrderApi {

    @Override
    public ResponseEntity order(OrderDTO orderDTO) {
        log.info("Order Params: {}", orderDTO.toString());
        /*if (true) {
            throw new OrderServiceRuntimeException(ErrorEnum.UNKNOWN);
        }*/
        return ResponseEntity.ok(OkEnum.GLOBAL_INSERT_OK);
    }
}
