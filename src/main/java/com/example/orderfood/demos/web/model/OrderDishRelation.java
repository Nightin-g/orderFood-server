package com.example.orderfood.demos.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDishRelation
{

    private BigInteger odID;
    private BigInteger dishId;
    private BigInteger orderId;

}
