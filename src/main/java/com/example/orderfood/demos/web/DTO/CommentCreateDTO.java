package com.example.orderfood.demos.web.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class CommentCreateDTO {
    private String context;
    private BigDecimal score;
    private BigInteger shopId;
    private BigInteger dishId;
}
