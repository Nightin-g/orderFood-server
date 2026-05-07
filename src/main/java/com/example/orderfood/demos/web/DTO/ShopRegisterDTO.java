package com.example.orderfood.demos.web.DTO;

import lombok.Data;

@Data
public class ShopRegisterDTO {
    private String shopName;
    private String shopAccount;
    private String password;
    private String confirmPassword;
    private Integer shopType;
    private String shopPhone;
    private Integer position;
    private String captcha;
    private String captchaKey;
}