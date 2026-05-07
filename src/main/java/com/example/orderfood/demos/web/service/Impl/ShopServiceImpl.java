package com.example.orderfood.demos.web.service.Impl;

import com.example.orderfood.demos.web.DTO.ShopLoginDTO;
import com.example.orderfood.demos.web.DTO.ShopRegisterDTO;
import com.example.orderfood.demos.web.DTO.ShopUpdateDTO;
import com.example.orderfood.demos.web.enums.ShopStatusEnum;
import com.example.orderfood.demos.web.mapper.ShopMapper;
import com.example.orderfood.demos.web.model.Shop;
import com.example.orderfood.demos.web.service.CaptchaService;
import com.example.orderfood.demos.web.service.ShopService;
import com.example.orderfood.demos.web.util.JwtUtils;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ShopServiceImpl implements ShopService {
    
    @Autowired
    private ShopMapper shopMapper;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private CaptchaService captchaService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Override
    public List<Shop> getPendingReviewShops() {
        return shopMapper.selectPendingReview();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R approveShop(BigInteger shopId) {
        // 1. 验证店铺是否存在
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return R.error(404, "店铺不存在");
        }
        
        // 2. 验证店铺状态是否为待审核
        if (shop.getShopStatus() != ShopStatusEnum.PENDING_REVIEW.getCode()) {
            return R.error(400, "店铺状态不是待审核");
        }
        
        // 3. 更新店铺状态为休息中
        shop.setShopStatus(ShopStatusEnum.RESTING.getCode());
        int result = shopMapper.updateById(shop);
        if (result <= 0) {
            return R.error(500, "审核通过失败");
        }
        
        return R.success("审核通过成功");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R rejectShop(BigInteger shopId) {
        // 1. 验证店铺是否存在
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return R.error(404, "店铺不存在");
        }
        
        // 2. 验证店铺状态是否为待审核
        if (shop.getShopStatus() != ShopStatusEnum.PENDING_REVIEW.getCode()) {
            return R.error(400, "店铺状态不是待审核");
        }
        
        // 3. 更新店铺状态为审核未通过
        shop.setShopStatus(ShopStatusEnum.REVIEW_FAILED.getCode());
        int result = shopMapper.updateById(shop);
        if (result <= 0) {
            return R.error(500, "审核不通过失败");
        }
        
        return R.success("审核不通过成功");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R shopLogin(ShopLoginDTO shopLoginDTO) {
        // 1. 验证码校验
        boolean captchaValid = captchaService.validateCaptcha(shopLoginDTO.getCaptchaKey(), shopLoginDTO.getCaptcha());
        if (!captchaValid) {
            return R.error(400, "验证码错误或已过期");
        }
        
        // 2. 根据店铺账号查询店铺
        Shop shop = shopMapper.selectByShopAccount(shopLoginDTO.getShopAccount());
        
        // 3. 验证店铺是否存在
        if (shop == null) {
            return R.error(401, "店铺不存在");
        }
        
        // 4. 验证密码
        if (!passwordEncoder.matches(shopLoginDTO.getPassword(), shop.getPassword())) {
            return R.error(401, "密码错误");
        }

        // 5. 验证店铺状态
        if (shop.getShopStatus() == ShopStatusEnum.PENDING_REVIEW.getCode()) {
            return R.error(403, "店铺正在审核中");
        }
        
        if (shop.getShopStatus() == ShopStatusEnum.REVIEW_FAILED.getCode()) {
            return R.error(403, "店铺审核未通过");
        }
        
        if (shop.getShopStatus() == ShopStatusEnum.PERMANENTLY_CLOSED.getCode()) {
            return R.error(403, "店铺已永久停业");
        }
        
        // 6. 生成JWT令牌
        String token = jwtUtils.generateToken(shop.getShopId().longValue(), shop.getShopAccount(), "shop");

        // 7. 将令牌存入Redis，设置过期时间为1小时
        String redisKey = "shop:token:" + shop.getShopId();
        redisTemplate.opsForValue().set(redisKey, token, 3600, TimeUnit.SECONDS);

        return R.success("登录成功").put("token", token).put("role", "shop");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R shopRegister(ShopRegisterDTO shopRegisterDTO) {
        // 1. 验证码校验
        boolean captchaValid = captchaService.validateCaptcha(shopRegisterDTO.getCaptchaKey(), shopRegisterDTO.getCaptcha());
        if (!captchaValid) {
            return R.error(400, "验证码错误或已过期");
        }
        
        // 2. 验证参数
        if (!shopRegisterDTO.getPassword().equals(shopRegisterDTO.getConfirmPassword())) {
            return R.error(400, "两次输入的密码不一致");
        }
        
        String shopAccount = shopRegisterDTO.getShopAccount();
        if (shopAccount == null || shopAccount.length() < 6) {
            return R.error(400, "账号长度不能少于6位");
        }
        
        String password = shopRegisterDTO.getPassword();
        if (password == null || password.length() < 6) {
            return R.error(400, "密码长度不能少于6位");
        }
        
        // 3. 尝试获取分布式锁
        String lockKey = "lock:shop:register:" + shopAccount;
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
            
            // 4. 双重检查：再次验证店铺账号是否已存在
            Shop existingShop = shopMapper.selectByShopAccount(shopAccount);
            if (existingShop != null) {
                return R.error(400, "店铺账号已存在");
            }
            
            // 5. 密码加密
            String encryptedPassword = passwordEncoder.encode(password);
            
            // 6. 创建店铺对象
            Shop shop = new Shop();
            shop.setShopName(shopRegisterDTO.getShopName());
            shop.setShopAccount(shopAccount);
            shop.setPassword(encryptedPassword);
            shop.setShopStatus(ShopStatusEnum.PENDING_REVIEW.getCode()); // 初始状态为待审核
            shop.setShopType(shopRegisterDTO.getShopType());
            shop.setOperating(0); // 初始为非营业状态
            shop.setShopSales(BigInteger.ZERO);
            shop.setDeliveryFee(new java.math.BigDecimal(0));
            shop.setShopPhone(shopRegisterDTO.getShopPhone());
            shop.setPosition(shopRegisterDTO.getPosition() != null ? shopRegisterDTO.getPosition() : 0);
            shop.setShopScore(new java.math.BigDecimal(0));
            shop.setCreateTime(LocalDateTime.now());
            
            // 7. 执行注册
            int result = shopMapper.insert(shop);
            if (result <= 0) {
                return R.error(500, "注册失败，请稍后重试");
            }
            
            return R.success("注册成功，等待审核");
            
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
    public R getCurrentShop() {
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BigInteger shopId = BigInteger.valueOf(shopIdLong);

        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return R.error(404, "店铺不存在");
        }

        shop.setPassword(null);
        return R.success(shop);
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
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateShopInfo(ShopUpdateDTO shopUpdateDTO) {
        // 1. 从SecurityContext中获取当前登录店铺的ID
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.math.BigInteger shopId = java.math.BigInteger.valueOf(shopIdLong);
        
        // 2. 根据店铺ID查询店铺是否存在
        Shop existingShop = shopMapper.selectById(shopId);
        if (existingShop == null) {
            return R.error(404, "店铺不存在");
        }
        
        // 3. 创建Shop对象并设置更新信息
        Shop shop = new Shop();
        shop.setShopId(shopId);
        
        // 只更新非空字段
        if (shopUpdateDTO.getShopName() != null) {
            shop.setShopName(shopUpdateDTO.getShopName());
        }
        if (shopUpdateDTO.getShopType() != null) {
            shop.setShopType(shopUpdateDTO.getShopType());
        }
        if (shopUpdateDTO.getShopPhone() != null) {
            shop.setShopPhone(shopUpdateDTO.getShopPhone());
        }
        if (shopUpdateDTO.getDeliveryFee() != null) {
            shop.setDeliveryFee(shopUpdateDTO.getDeliveryFee());
        }
        if (shopUpdateDTO.getShopPhoto() != null) {
            shop.setShopPhoto(shopUpdateDTO.getShopPhoto());
        }
        if (shopUpdateDTO.getOperating() != null) {
            shop.setOperating(shopUpdateDTO.getOperating());
        }
        if (shopUpdateDTO.getPosition() != null) {
            shop.setPosition(shopUpdateDTO.getPosition());
        }
        
        // 4. 执行更新操作
        int result = shopMapper.updateById(shop);
        if (result <= 0) {
            return R.error(500, "更新失败，请稍后重试");
        }
        
        return R.success("更新成功");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R openShop() {
        // 1. 从SecurityContext中获取当前登录店铺的ID
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.math.BigInteger shopId = java.math.BigInteger.valueOf(shopIdLong);
        
        // 2. 根据店铺ID查询店铺是否存在
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return R.error(404, "店铺不存在");
        }
        
        // 3. 验证店铺状态是否可以转换为营业
        int currentStatus = shop.getShopStatus();
        if (currentStatus != ShopStatusEnum.RESTING.getCode() && currentStatus != ShopStatusEnum.TEMPORARILY_CLOSED.getCode()) {
            return R.error(400, "只有休息中或暂时歇业的店铺可以开始营业");
        }
        
        // 4. 更新店铺状态为营业
        shop.setShopStatus(ShopStatusEnum.OPEN.getCode());
        shop.setOperating(1); // 设置为营业状态
        int result = shopMapper.updateById(shop);
        if (result <= 0) {
            return R.error(500, "状态更新失败，请稍后重试");
        }
        
        return R.success("店铺已开始营业");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R restShop() {
        // 1. 从SecurityContext中获取当前登录店铺的ID
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.math.BigInteger shopId = java.math.BigInteger.valueOf(shopIdLong);
        
        // 2. 根据店铺ID查询店铺是否存在
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return R.error(404, "店铺不存在");
        }
        
        // 3. 验证店铺状态是否可以转换为休息
        int currentStatus = shop.getShopStatus();
        if (currentStatus != ShopStatusEnum.OPEN.getCode()) {
            return R.error(400, "只有正在营业的店铺可以进入休息");
        }
        
        // 4. 更新店铺状态为休息
        shop.setShopStatus(ShopStatusEnum.RESTING.getCode());
        shop.setOperating(0); // 设置为非营业状态
        int result = shopMapper.updateById(shop);
        if (result <= 0) {
            return R.error(500, "状态更新失败，请稍后重试");
        }
        
        return R.success("店铺已进入休息");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R temporarilyCloseShop() {
        // 1. 从SecurityContext中获取当前登录店铺的ID
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.math.BigInteger shopId = java.math.BigInteger.valueOf(shopIdLong);
        
        // 2. 根据店铺ID查询店铺是否存在
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return R.error(404, "店铺不存在");
        }
        
        // 3. 验证店铺状态是否可以转换为暂时歇业
        int currentStatus = shop.getShopStatus();
        if (currentStatus != ShopStatusEnum.OPEN.getCode() && currentStatus != ShopStatusEnum.RESTING.getCode()) {
            return R.error(400, "只有正在营业或休息中的店铺可以暂时歇业");
        }
        
        // 4. 更新店铺状态为暂时歇业
        shop.setShopStatus(ShopStatusEnum.TEMPORARILY_CLOSED.getCode());
        shop.setOperating(0); // 设置为非营业状态
        int result = shopMapper.updateById(shop);
        if (result <= 0) {
            return R.error(500, "状态更新失败，请稍后重试");
        }
        
        return R.success("店铺已暂时歇业");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R resumeShop() {
        // 1. 从SecurityContext中获取当前登录店铺的ID
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.math.BigInteger shopId = java.math.BigInteger.valueOf(shopIdLong);
        
        // 2. 根据店铺ID查询店铺是否存在
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return R.error(404, "店铺不存在");
        }
        
        // 3. 验证店铺状态是否可以转换为休息
        int currentStatus = shop.getShopStatus();
        if (currentStatus != ShopStatusEnum.TEMPORARILY_CLOSED.getCode()) {
            return R.error(400, "只有暂时歇业的店铺可以恢复营业");
        }
        
        // 4. 更新店铺状态为休息
        shop.setShopStatus(ShopStatusEnum.RESTING.getCode());
        int result = shopMapper.updateById(shop);
        if (result <= 0) {
            return R.error(500, "状态更新失败，请稍后重试");
        }
        
        return R.success("店铺已恢复营业");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R permanentlyCloseShop() {
        // 1. 从SecurityContext中获取当前登录店铺的ID
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.math.BigInteger shopId = java.math.BigInteger.valueOf(shopIdLong);
        
        // 2. 根据店铺ID查询店铺是否存在
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return R.error(404, "店铺不存在");
        }
        
        // 3. 验证店铺状态是否可以转换为永久停业
        int currentStatus = shop.getShopStatus();
        if (currentStatus == ShopStatusEnum.PERMANENTLY_CLOSED.getCode()) {
            return R.error(400, "店铺已经永久停业");
        }
        
        // 4. 更新店铺状态为永久停业
        shop.setShopStatus(ShopStatusEnum.PERMANENTLY_CLOSED.getCode());
        shop.setOperating(0); // 设置为非营业状态
        int result = shopMapper.updateById(shop);
        if (result <= 0) {
            return R.error(500, "状态更新失败，请稍后重试");
        }
        
        return R.success("店铺已永久停业");
    }
}