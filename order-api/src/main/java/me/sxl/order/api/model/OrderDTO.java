package me.sxl.order.api.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDTO {

    private BigDecimal payAmount;

    private String userId;

}
