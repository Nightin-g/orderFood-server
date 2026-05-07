package com.example.orderfood.demos.web.service.Impl;

import com.example.orderfood.demos.web.DTO.UserLoginDTO;
import com.example.orderfood.demos.web.DTO.UserRegisterDTO;
import com.example.orderfood.demos.web.DTO.UserUpdateDTO;
import com.example.orderfood.demos.web.enums.UserStatusEnum;
import com.example.orderfood.demos.web.mapper.UserMapper;
import com.example.orderfood.demos.web.model.User;
import com.example.orderfood.demos.web.service.CaptchaService;
import com.example.orderfood.demos.web.service.UserService;
import com.example.orderfood.demos.web.util.JwtUtils;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private CaptchaService captchaService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R userLogin(UserLoginDTO userLoginDTO) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUserAccount(userLoginDTO.getUserAccount());
        
        // 2. 验证用户是否存在
        if (user == null) {
            return R.error(401, "用户不存在");
        }
        
        // 3. 验证密码
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            return R.error(401, "密码错误");
        }

        // 4. 验证用户状态
        if (user.getUserStatus() == UserStatusEnum.RESTRICTED.getCode()) {
            return R.error(403, "用户账号已被禁用");
        }
        
        // 5. 生成JWT令牌
        String token = jwtUtils.generateToken(user.getUserId().longValue(), user.getUserAccount());
        
        // 6. 将令牌存入Redis，设置过期时间为1小时
        String redisKey = "user:token:" + user.getUserId();
        redisTemplate.opsForValue().set(redisKey, token, 3600, TimeUnit.SECONDS);
        
        return R.success("登录成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R userRegister(UserRegisterDTO userRegisterDTO) {
        // 1. 验证参数
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            return R.error(400, "两次输入的密码不一致");
        }
        
        String userAccount = userRegisterDTO.getUserAccount();
        if (userAccount == null || userAccount.length() < 6) {
            return R.error(400, "账号长度不能少于6位");
        }
        
        String password = userRegisterDTO.getPassword();
        if (password == null || password.length() < 6) {
            return R.error(400, "密码长度不能少于6位");
        }
        
        // 2. 尝试获取分布式锁
        String lockKey = "lock:register:" + userAccount;
        String requestId = java.util.UUID.randomUUID().toString();
        boolean locked = false;
        
        try {
            // 尝试获取锁，最多重试5次
            for (int i = 0; i < 5; i++) {
                locked = tryLock(lockKey, requestId, 10);
                if (locked) {
                    break;
                }
                // 重试间隔100ms
                Thread.sleep(100);
            }
            
            if (!locked) {
                return R.error(500, "系统繁忙，请稍后重试");
            }
            
            // 3. 双重检查：再次验证用户名是否已存在
            User existingUser = userMapper.selectByUserAccount(userAccount);
            if (existingUser != null) {
                return R.error(400, "账号已存在");
            }
            
            // 4. 密码加密
            String encryptedPassword = passwordEncoder.encode(password);
            
            // 5. 创建用户对象
            User user = new User();
            user.setUserAccount(userAccount);
            user.setPassword(encryptedPassword);
            user.setUserName(userRegisterDTO.getUserName() != null ? userRegisterDTO.getUserName() : userAccount);
            user.setPhoneNum(userRegisterDTO.getPhoneNum());
            user.setUserStatus(0); // 1表示启用
            user.setCreateTime(java.time.LocalDateTime.now());
            
            // 6. 执行注册
            int result = userMapper.insert(user);
            if (result <= 0) {
                return R.error(500, "注册失败，请稍后重试");
            }
            
            return R.success("注册成功");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return R.error(500, "系统繁忙，请稍后重试");
        } finally {
            // 释放锁
            if (locked) {
                releaseLock(lockKey, requestId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updatePassword(UserUpdateDTO userUpdateDTO) {
        // 1. 验证码校验
        boolean captchaValid = captchaService.validateCaptcha(userUpdateDTO.getCaptchaKey(), userUpdateDTO.getCaptcha());
        if (!captchaValid) {
            return R.error(400, "验证码错误或已过期");
        }
        
        // 2. 获取参数
        String oldPassword = userUpdateDTO.getPassword();
        String newPassword = userUpdateDTO.getNewPassword();
        BigInteger userId = userUpdateDTO.getUserId();
        
        // 3. 参数验证
        if (oldPassword == null || oldPassword.length() < 6) {
            return R.error(400, "旧密码长度不能少于6位");
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            return R.error(400, "新密码长度不能少于6位");
        }
        
        // 4. 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return R.error(404, "用户不存在");
        }
        
        // 5. 验证旧密码
        boolean match = passwordEncoder.matches(oldPassword, user.getPassword());
        if (!match) {
            return R.error(401, "旧密码错误");
        }
        
        // 6. 加密新密码
        String encryptedNewPassword = passwordEncoder.encode(newPassword);
        
        // 7. 更新密码
        // 设置加密后的新密码到DTO中
        userUpdateDTO.setPassword(encryptedNewPassword);
        // 设置用户ID到DTO中
        userUpdateDTO.setUserId(userId);
        
        // 调用updatePassword方法更新密码
        int result = userMapper.updatePassword(userUpdateDTO);
        if (result <= 0) {
            return R.error(500, "密码更新失败，请稍后重试");
        }
        
        
        return R.success("密码更新成功");
    }

    @Override
    public R updateUserInfo(UserUpdateDTO userUpdateDTO) {
        // 1. 从SecurityContext中获取当前登录用户的ID
        Long userIdLong = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BigInteger userId = BigInteger.valueOf(userIdLong);
        
        // 2. 根据用户ID查询用户是否存在
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            return R.error(404, "用户不存在");
        }
        
        // 3. 创建User对象并设置更新信息
        User user = new User();
        user.setUserId(userId);
        user.setUserName(userUpdateDTO.getUserName());
        user.setSex(userUpdateDTO.getSex());
        user.setAddress(userUpdateDTO.getAddress());
        user.setUserPhoto(userUpdateDTO.getUserPhoto());
        
        // 4. 执行更新操作
        int result = userMapper.updateUserInfo(user);
        if (result <= 0) {
            return R.error(500, "更新失败，请稍后重试");
        }
        
        return R.success("更新成功");
    }
    
    /**
     * 尝试获取分布式锁
     * @param lockKey 锁键
     * @param requestId 请求ID
     * @param expireTime 过期时间（秒）
     * @return 是否获取成功
     */
    private boolean tryLock(String lockKey, String requestId, long expireTime) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }
    
    /**
     * 释放分布式锁
     * @param lockKey 锁键
     * @param requestId 请求ID
     */
    private void releaseLock(String lockKey, String requestId) {
        try {
            String currentRequestId = redisTemplate.opsForValue().get(lockKey);
            if (requestId.equals(currentRequestId)) {
                redisTemplate.delete(lockKey);
            }
        } catch (Exception e) {
            // 记录日志，不影响主流程
            e.printStackTrace();
        }
    }
}