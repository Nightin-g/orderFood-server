package com.example.orderfood.demos.web.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户注册DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    /**
     * 用户名
     */
    private String userAccount;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 确认密码
     */
    private String confirmPassword;
    
    /**
     * 手机号
     */
    private String phoneNum;
    
    /**
     * 用户名
     */
    private String userName;
}