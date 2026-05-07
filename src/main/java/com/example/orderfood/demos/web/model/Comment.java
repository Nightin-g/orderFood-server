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
public class Comment {
    private BigInteger commentId;
    private String context;
    private BigDecimal score;
    private BigInteger shopId;
    private BigInteger userId;
    private BigInteger dishId;
    private LocalDateTime createTime;
}
