package com.example.orderfood.demos.web.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class OrderItemDTO {
    private BigInteger dishId;
    private String dishName;
    private Integer quantity;
    private BigDecimal price;
}
