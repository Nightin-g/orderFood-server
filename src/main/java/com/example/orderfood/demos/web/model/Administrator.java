package com.example.orderfood.demos.web.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Administrator
{
    private BigInteger adminId;
    private String adminName;
    private String phoneNum;
    private String adminAccount;
    private String password;
    private Integer jurisdiction;//管理区域
}
