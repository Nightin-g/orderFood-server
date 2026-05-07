package com.example.orderfood.demos.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ShopActivityRelation
{
    private BigInteger saId;
    private BigInteger activityId;
    private BigInteger shopId;
}
