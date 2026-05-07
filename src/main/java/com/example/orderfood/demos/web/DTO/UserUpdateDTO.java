package com.example.orderfood.demos.web.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户修改信息DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 性别
     */
    private Integer sex;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 用户头像
     */
    private String userPhoto;

    private String phone;

    private String password;

    private String newPassword;
    
    /**
     * 验证码
     */
    private String captcha;
    
    /**
     * 验证码唯一标识
     */
    private String captchaKey;
    
    /**
     * 用户ID（用于密码更新）
     */
    private java.math.BigInteger userId;
}