package com.example.orderfood.demos.web.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码生成工具类
 * 用于生成图形验证码
 */
public class CaptchaUtil {
    
    /**
     * 验证码字符集
     */
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz123456789";
    
    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();
    
    /**
     * 默认验证码宽度
     */
    public static final int DEFAULT_WIDTH = 120;
    
    /**
     * 默认验证码高度
     */
    public static final int DEFAULT_HEIGHT = 40;
    
    /**
     * 默认验证码长度
     */
    public static final int DEFAULT_LENGTH = 4;
    
    /**
     * 生成随机验证码文本
     * @param length 验证码长度
     * @return 验证码文本
     */
    public static String generateCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(CAPTCHA_CHARS.charAt(RANDOM.nextInt(CAPTCHA_CHARS.length())));
        }
        return code.toString();
    }
    
    /**
     * 生成图形验证码
     * @param code 验证码文本
     * @param width 图片宽度
     * @param height 图片高度
     * @return 图形验证码图片
     */
    public static BufferedImage generateCaptcha(String code, int width, int height) {
        // 创建图片对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        
        // 设置背景色
        g.setColor(getRandomColor(200, 250));
        g.fillRect(0, 0, width, height);
        
        // 设置边框
        g.setColor(getRandomColor(180, 230));
        g.drawRect(0, 0, width - 1, height - 1);
        
        // 添加干扰线
        for (int i = 0; i < 8; i++) {
            g.setColor(getRandomColor(160, 200));
            int x1 = RANDOM.nextInt(width);
            int y1 = RANDOM.nextInt(height);
            int x2 = RANDOM.nextInt(width);
            int y2 = RANDOM.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }
        
        // 添加噪点
        for (int i = 0; i < 100; i++) {
            g.setColor(getRandomColor(140, 180));
            int x = RANDOM.nextInt(width);
            int y = RANDOM.nextInt(height);
            g.fillOval(x, y, 2, 2);
        }
        
        // 绘制验证码文本
        g.setFont(new Font("Arial", Font.BOLD, 20));
        for (int i = 0; i < code.length(); i++) {
            g.setColor(getRandomColor(80, 160));
            g.drawString(String.valueOf(code.charAt(i)), 25 * i + 10, 25);
        }
        
        // 释放资源
        g.dispose();
        
        return image;
    }
    
    /**
     * 生成随机颜色
     * @param min 最小值
     * @param max 最大值
     * @return 随机颜色
     */
    private static Color getRandomColor(int min, int max) {
        min = Math.max(min, 0);
        max = Math.min(max, 255);
        int r = min + RANDOM.nextInt(max - min + 1);
        int g = min + RANDOM.nextInt(max - min + 1);
        int b = min + RANDOM.nextInt(max - min + 1);
        return new Color(r, g, b);
    }
    
    /**
     * 生成默认配置的验证码
     * @return 验证码文本
     */
    public static String generateCode() {
        return generateCode(DEFAULT_LENGTH);
    }
    
    /**
     * 生成默认配置的图形验证码
     * @param code 验证码文本
     * @return 图形验证码图片
     */
    public static BufferedImage generateCaptcha(String code) {
        return generateCaptcha(code, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}