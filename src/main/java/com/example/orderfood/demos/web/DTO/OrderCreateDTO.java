package com.example.orderfood.demos.web.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateDTO {
    private List<OrderItemDTO> items;
    private BigDecimal totalPrice;
    private String address;
    private String phone;
    private String remark;
}
