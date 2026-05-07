package com.example.orderfood.demos.web.service;

import java.util.Map;

/**
 * 验证码服务接口
 * 提供验证码生成、存储和验证功能
 */
public interface CaptchaService {
    
    /**
     * 生成验证码
     * @return 返回包含验证码key和图片的Map
     *         key: captchaKey - 验证码唯一标识
     *         value: 验证码图片的字节数组
     */
    Map<String, Object> generateCaptcha();
    
    /**
     * 验证验证码
     * @param captchaKey 验证码唯一标识
     * @param captcha 验证码文本
     * @return 验证码是否有效
     */
    boolean validateCaptcha(String captchaKey, String captcha);
}