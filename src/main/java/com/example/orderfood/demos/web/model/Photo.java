package com.example.orderfood.demos.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo
{
    private BigInteger photoId;
    private BigInteger commentId;
    private BigInteger shopId;
    private BigInteger dishId;
    private BigInteger userId;
    private BigInteger activityId;
    private String url;
}
