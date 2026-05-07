package com.example.orderfood.demos.web.Controller;

import com.example.orderfood.demos.web.service.CaptchaService;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 验证码控制器
 * 提供获取验证码的HTTP接口
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    
    /**
     * 验证码服务
     */
    @Autowired
    private CaptchaService captchaService;
    
    /**
     * 获取验证码
     * @return ResponseEntity 包含验证码图片和key的响应
     */
    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateCaptcha() {
        // 生成验证码
        Map<String, Object> captchaData = captchaService.generateCaptcha();
        
        // 获取验证码图片字节数组
        byte[] imageBytes = (byte[]) captchaData.get("image");
        String captchaKey = (String) captchaData.get("captchaKey");
        
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(imageBytes.length);
        // 将验证码key添加到响应头中，方便客户端获取
        headers.add("Captcha-Key", captchaKey);
        headers.setCacheControl("no-store, no-cache, must-revalidate, max-age=0");
        headers.setPragma("no-cache");
        headers.setExpires(0L);
        
        // 返回响应
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
    
    /**
     * 验证验证码（可选，客户端可以调用此接口验证验证码，也可以在业务接口中验证）
     * @param captchaKey 验证码唯一标识
     * @param captcha 验证码文本
     * @return 验证结果
     */
    @GetMapping("/validate")
    public R validateCaptcha(String captchaKey, String captcha) {
        boolean isValid = captchaService.validateCaptcha(captchaKey, captcha);
        if (isValid) {
            return R.success("验证码验证成功");
        } else {
            return R.error(400, "验证码错误或已过期");
        }
    }
}