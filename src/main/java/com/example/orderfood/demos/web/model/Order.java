package com.example.orderfood.demos.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order
{
    private BigInteger orderId;
    private BigInteger orderStatus;
    private BigDecimal orderPrice;
    private String orderNum;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
    private BigInteger userId;

}
