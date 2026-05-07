package com.example.orderfood.demos.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private BigInteger userId;
    private String userAccount;
    private String password;
    private String userName;
    private String phoneNum;
    private String address;
    private Integer sex;
    private String userPhoto;
    private LocalDateTime createTime;
    private Integer userStatus;

}
