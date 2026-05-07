package com.example.orderfood.demos.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Activity
{
    private BigInteger activityId;
    private String activityName;
    private Integer couponType;
    private BigInteger shopId;
    private Integer activityStatus;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
