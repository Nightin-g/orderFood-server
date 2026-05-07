package com.example.orderfood.demos.web.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopUpdateDTO {
    private String shopName;
    private Integer shopType;
    private String shopPhone;
    private BigDecimal deliveryFee;
    private String shopPhoto;
    private Integer operating;
    private Integer position;
}