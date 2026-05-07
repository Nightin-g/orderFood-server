package com.example.orderfood.demos.web.DTO;

import lombok.Data;

@Data
public class ShopLoginDTO {
    private String shopAccount;
    private String password;
    private String captcha;
    private String captchaKey;
}