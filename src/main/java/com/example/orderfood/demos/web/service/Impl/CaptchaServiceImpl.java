package com.example.orderfood.demos.web.service.Impl;

import com.example.orderfood.demos.web.service.CaptchaService;
import com.example.orderfood.demos.web.util.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 * 处理验证码的生成、存储和验证逻辑
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {
    
    /**
     * Redis模板，用于存储验证码
     */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    /**
     * 验证码有效期（分钟）
     */
    private static final long CAPTCHA_EXPIRE_TIME = 5;
    
    /**
     * Redis中验证码的key前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    
    /**
     * 生成验证码
     * @return 返回包含验证码key和图片的Map
     */
    @Override
    public Map<String, Object> generateCaptcha() {
        // 生成随机验证码文本
        String code = CaptchaUtil.generateCode();
        
        // 生成唯一标识
        String captchaKey = UUID.randomUUID().toString();
        
        // 将验证码存储到Redis中，设置过期时间
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        redisTemplate.opsForValue().set(redisKey, code, CAPTCHA_EXPIRE_TIME, TimeUnit.MINUTES);
        
        // 生成图形验证码
        BufferedImage image = CaptchaUtil.generateCaptcha(code);
        
        // 将图片转换为字节数组
        byte[] imageBytes = convertImageToBytes(image);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("image", imageBytes);
        
        return result;
    }
    
    /**
     * 验证验证码
     * @param captchaKey 验证码唯一标识
     * @param captcha 验证码文本
     * @return 验证码是否有效
     */
    @Override
    public boolean validateCaptcha(String captchaKey, String captcha) {
        // 验证参数
        if (captchaKey == null || captchaKey.isEmpty() || captcha == null || captcha.isEmpty()) {
            return false;
        }
        
        // 构建Redis中的key
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        
        // 从Redis中获取验证码
        String storedCaptcha = redisTemplate.opsForValue().get(redisKey);
        
        // 验证验证码是否存在且匹配
        if (storedCaptcha != null && storedCaptcha.equalsIgnoreCase(captcha)) {
            // 验证码验证成功后，从Redis中删除，防止重复使用
            redisTemplate.delete(redisKey);
            return true;
        }
        
        return false;
    }
    
    /**
     * 将BufferedImage转换为字节数组
     * @param image 图片对象
     * @return 图片字节数组
     */
    private byte[] convertImageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 将图片写入输出流
            ImageIO.write(image, "png", outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("图片转换失败", e);
        }
        // 忽略关闭异常
    }
}