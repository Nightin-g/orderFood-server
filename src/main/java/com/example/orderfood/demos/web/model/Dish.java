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
public class Dish
{
    private BigInteger dishId;
    private String dishName;
    private Integer dishStatus;
    private Integer forSale;
    private BigInteger categoryId;
    private BigDecimal price;
    private BigInteger dishSales;
    private BigDecimal dishScore;
    private BigInteger dishPhotoId;
    private String dishPhoto;
    private String ingredients;//食材成分
    private LocalDateTime createTime;
    private BigInteger shopId;
    private Integer dishScorePerson;
}
