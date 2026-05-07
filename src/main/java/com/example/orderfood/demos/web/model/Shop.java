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
public class Shop {

    private BigInteger shopId;
    private String shopName;
    private String shopAccount;
    private String password;
    private Integer shopStatus;
    private Integer shopType;
    private Integer operating;
    private BigInteger shopSales;
    private BigDecimal deliveryFee;
    private String shopPhone;
    private Integer position;
    private BigDecimal shopScore;
    private String shopPhoto;
    private BigInteger shopPhotoId;
    private LocalDateTime createTime;
    private Integer shopScorePerson;
}
