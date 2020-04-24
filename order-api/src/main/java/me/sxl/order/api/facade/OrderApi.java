package me.sxl.order.api.facade;

import me.sxl.common.model.ResponseEntity;
import me.sxl.order.api.model.OrderDTO;

public interface OrderApi {

    ResponseEntity order(OrderDTO orderDTO);

}
