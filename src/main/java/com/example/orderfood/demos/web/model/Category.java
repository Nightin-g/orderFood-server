package com.example.orderfood.demos.web.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {


    private BigInteger categoryId;
    private String categoryName;
    private BigInteger shopId;
    private Integer categoryStatus;
    private LocalDateTime createTime;

}
