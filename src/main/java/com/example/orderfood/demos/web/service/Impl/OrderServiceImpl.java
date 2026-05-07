package com.example.orderfood.demos.web.service.Impl;

import com.example.orderfood.demos.web.DTO.OrderCreateDTO;
import com.example.orderfood.demos.web.DTO.OrderItemDTO;
import com.example.orderfood.demos.web.enums.OrderStatusEnum;
import com.example.orderfood.demos.web.exception.BusinessException;
import com.example.orderfood.demos.web.mapper.OrderDishRelationMapper;
import com.example.orderfood.demos.web.mapper.OrderMapper;
import com.example.orderfood.demos.web.mapper.ShopMapper;
import com.example.orderfood.demos.web.model.Order;
import com.example.orderfood.demos.web.model.OrderDishRelation;
import com.example.orderfood.demos.web.service.OrderService;
import com.example.orderfood.demos.web.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    
    private static final String ORDER_LOCK_PREFIX = "order:lock:";
    private static final int LOCK_EXPIRATION = 10; // 锁过期时间（秒）
    private static final int ORDER_LOCK_EXPIRATION = 30; // 订单锁过期时间（分钟）

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderDishRelationMapper orderDishRelationMapper;
    
    @Autowired
    private ShopMapper shopMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R createOrder(OrderCreateDTO orderCreateDTO) {
        // 1. 获取当前登录用户的ID
        BigInteger userId = getCurrentUserId();
        
        // 2. 生成订单号
        String orderNum = generateOrderNum();
        
        // 3. 创建订单对象
        Order order = new Order();
        order.setOrderStatus(BigInteger.valueOf(OrderStatusEnum.PENDING_PAYMENT.getCode()));
        order.setOrderPrice(orderCreateDTO.getTotalPrice());
        order.setOrderNum(orderNum);
        order.setCreateTime(LocalDateTime.now());
        order.setUserId(userId);
        
        // 4. 保存订单
        int result = orderMapper.insert(order);
        if (result <= 0) {
            logger.error("订单创建失败，orderCreateDTO: {}", orderCreateDTO);
            throw new BusinessException(500, "订单创建失败，请稍后重试");
        }
        
        // 5. 保存订单商品关联关系
        saveOrderDishRelations(order.getOrderId(), orderCreateDTO.getItems());
        
        // 6. 生成订单锁，防止并发操作
        String orderLockKey = ORDER_LOCK_PREFIX + order.getOrderId();
        redisTemplate.opsForValue().set(orderLockKey, UUID.randomUUID().toString(), ORDER_LOCK_EXPIRATION, TimeUnit.MINUTES);
        
        logger.info("订单创建成功，orderId: {}, orderNum: {}", order.getOrderId(), orderNum);
        return R.success("订单创建成功", order);
    }

    @Override
    public List<Order> getUserOrders() {
        // 获取当前登录用户的ID
        BigInteger userId = getCurrentUserId();
        
        // 查询用户的所有订单
        // 注意：实际项目中需要在OrderMapper中添加selectByUserId方法
        return null;
    }

    @Override
    public R getOrderDetail(BigInteger orderId) {
        // 1. 查询订单详情
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        // 2. 验证订单是否属于当前用户
        BigInteger userId = getCurrentUserId();
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权查看该订单");
        }
        
        // 3. 查询订单商品详情
        // 注意：实际项目中需要查询OrderDishRelation和对应的商品信息
        
        return R.success("查询订单详情成功", order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R cancelOrder(BigInteger orderId) {
        String orderLockKey = ORDER_LOCK_PREFIX + orderId;
        if (!tryLock(orderLockKey, LOCK_EXPIRATION)) {
            throw new BusinessException(500, "系统繁忙，请稍后重试");
        }
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException(404, "订单不存在");
            }
            
            // 2. 验证订单是否属于当前用户
            BigInteger userId = getCurrentUserId();
            if (!order.getUserId().equals(userId)) {
                throw new BusinessException(403, "无权操作该订单");
            }
            
            // 3. 验证订单状态是否可以取消
            int currentStatus = order.getOrderStatus().intValue();
            if (currentStatus != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
                throw new BusinessException(400, "只有待付款的订单可以取消");
            }
            
            // 4. 更新订单状态
            order.setOrderStatus(BigInteger.valueOf(OrderStatusEnum.CANCELLED.getCode()));
            order.setFinishTime(LocalDateTime.now());
            int result = orderMapper.updateById(order);
            if (result <= 0) {
                logger.error("取消订单失败，orderId: {}", orderId);
                throw new BusinessException(500, "取消订单失败，请稍后重试");
            }
            
            logger.info("订单取消成功，orderId: {}", orderId);
            return R.success("订单已取消");
        } finally {
            // 释放锁
            redisTemplate.delete(orderLockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R payOrder(BigInteger orderId) {
        String orderLockKey = ORDER_LOCK_PREFIX + orderId;
        if (!tryLock(orderLockKey, LOCK_EXPIRATION)) {
            throw new BusinessException(500, "系统繁忙，请稍后重试");
        }
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException(404, "订单不存在");
            }
            
            // 2. 验证订单是否属于当前用户
            BigInteger userId = getCurrentUserId();
            if (!order.getUserId().equals(userId)) {
                throw new BusinessException(403, "无权操作该订单");
            }
            
            // 3. 验证订单状态是否可以支付
            int currentStatus = order.getOrderStatus().intValue();
            if (currentStatus != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
                throw new BusinessException(400, "只有待付款的订单可以支付");
            }
            
            // 4. 更新订单状态
            order.setOrderStatus(BigInteger.valueOf(OrderStatusEnum.PAID.getCode()));
            int result = orderMapper.updateById(order);
            if (result <= 0) {
                logger.error("支付订单失败，orderId: {}", orderId);
                throw new BusinessException(500, "支付订单失败，请稍后重试");
            }
            
            logger.info("订单支付成功，orderId: {}", orderId);
            return R.success("订单支付成功");
        } finally {
            // 释放锁
            redisTemplate.delete(orderLockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R confirmPickup(BigInteger orderId) {
        String orderLockKey = ORDER_LOCK_PREFIX + orderId;
        if (!tryLock(orderLockKey, LOCK_EXPIRATION)) {
            throw new BusinessException(500, "系统繁忙，请稍后重试");
        }
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException(404, "订单不存在");
            }
            
            // 2. 验证订单是否属于当前用户
            BigInteger userId = getCurrentUserId();
            if (!order.getUserId().equals(userId)) {
                throw new BusinessException(403, "无权操作该订单");
            }
            
            // 3. 验证订单状态是否可以确认取餐
            int currentStatus = order.getOrderStatus().intValue();
            if (currentStatus != OrderStatusEnum.READY_FOR_PICKUP.getCode()) {
                throw new BusinessException(400, "只有待取餐的订单可以确认取餐");
            }
            
            // 4. 更新订单状态
            order.setOrderStatus(BigInteger.valueOf(OrderStatusEnum.PICKED_UP_UNEVALUATED.getCode()));
            order.setFinishTime(LocalDateTime.now());
            int result = orderMapper.updateById(order);
            if (result <= 0) {
                logger.error("确认取餐失败，orderId: {}", orderId);
                throw new BusinessException(500, "确认取餐失败，请稍后重试");
            }
            
            logger.info("订单确认取餐成功，orderId: {}", orderId);
            return R.success("已确认取餐");
        } finally {
            // 释放锁
            redisTemplate.delete(orderLockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R evaluateOrder(BigInteger orderId, Integer score, String comment) {
        String orderLockKey = ORDER_LOCK_PREFIX + orderId;
        if (!tryLock(orderLockKey, LOCK_EXPIRATION)) {
            throw new BusinessException(500, "系统繁忙，请稍后重试");
        }
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException(404, "订单不存在");
            }
            
            // 2. 验证订单是否属于当前用户
            BigInteger userId = getCurrentUserId();
            if (!order.getUserId().equals(userId)) {
                throw new BusinessException(403, "无权操作该订单");
            }
            
            // 3. 验证订单状态是否可以评价
            int currentStatus = order.getOrderStatus().intValue();
            if (currentStatus != OrderStatusEnum.PICKED_UP_UNEVALUATED.getCode()) {
                throw new BusinessException(400, "只有已取餐未评价的订单可以评价");
            }
            
            // 4. 更新订单状态
            order.setOrderStatus(BigInteger.valueOf(OrderStatusEnum.EVALUATED.getCode()));
            int result = orderMapper.updateById(order);
            if (result <= 0) {
                logger.error("评价订单失败，orderId: {}", orderId);
                throw new BusinessException(500, "评价订单失败，请稍后重试");
            }
            
            // 5. 保存评价信息
            // 注意：实际项目中需要创建评价表和对应的Mapper
            
            logger.info("订单评价成功，orderId: {}", orderId);
            return R.success("评价成功");
        } finally {
            // 释放锁
            redisTemplate.delete(orderLockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R applyRefund(BigInteger orderId, String reason) {
        String orderLockKey = ORDER_LOCK_PREFIX + orderId;
        if (!tryLock(orderLockKey, LOCK_EXPIRATION)) {
            throw new BusinessException(500, "系统繁忙，请稍后重试");
        }
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException(404, "订单不存在");
            }
            
            // 2. 验证订单是否属于当前用户
            BigInteger userId = getCurrentUserId();
            if (!order.getUserId().equals(userId)) {
                throw new BusinessException(403, "无权操作该订单");
            }
            
            // 3. 验证订单状态是否可以申请退款
            int currentStatus = order.getOrderStatus().intValue();
            if (currentStatus != OrderStatusEnum.PAID.getCode() && currentStatus != OrderStatusEnum.PREPARING.getCode()) {
                throw new BusinessException(400, "只有已付款或待出餐的订单可以申请退款");
            }
            
            // 4. 更新订单状态
            order.setOrderStatus(BigInteger.valueOf(OrderStatusEnum.REFUNDING.getCode()));
            int result = orderMapper.updateById(order);
            if (result <= 0) {
                logger.error("申请退款失败，orderId: {}", orderId);
                throw new BusinessException(500, "申请退款失败，请稍后重试");
            }
            
            // 5. 保存退款申请信息
            // 注意：实际项目中需要创建退款申请表和对应的Mapper
            
            logger.info("退款申请已提交，orderId: {}", orderId);
            return R.success("退款申请已提交");
        } finally {
            // 释放锁
            redisTemplate.delete(orderLockKey);
        }
    }

    @Override
    public List<Order> getShopOrders() {
        // 从SecurityContext中获取当前登录店铺的ID
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BigInteger shopId = BigInteger.valueOf(shopIdLong);
        
        // 查询店铺的所有订单
        // 注意：实际项目中需要在OrderMapper中添加selectByShopId方法
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R acceptOrder(BigInteger orderId) {
        String orderLockKey = ORDER_LOCK_PREFIX + orderId;
        if (!tryLock(orderLockKey, LOCK_EXPIRATION)) {
            throw new BusinessException(500, "系统繁忙，请稍后重试");
        }
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException(404, "订单不存在");
            }
            
            // 2. 验证订单状态是否可以接单
            int currentStatus = order.getOrderStatus().intValue();
            if (currentStatus != OrderStatusEnum.PAID.getCode()) {
                throw new BusinessException(400, "只有已付款的订单可以接单");
            }
            
            // 3. 更新订单状态
            order.setOrderStatus(BigInteger.valueOf(OrderStatusEnum.PREPARING.getCode()));
            int result = orderMapper.updateById(order);
            if (result <= 0) {
                logger.error("接单失败，orderId: {}", orderId);
                throw new BusinessException(500, "接单失败，请稍后重试");
            }
            
            logger.info("订单已接单，orderId: {}", orderId);
            return R.success("订单已接单");
        } finally {
            // 释放锁
            redisTemplate.delete(orderLockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R rejectOrder(BigInteger orderId) {
        String orderLockKey = ORDER_LOCK_PREFIX + orderId;
        if (!tryLock(orderLockKey, LOCK_EXPIRATION)) {
            throw new BusinessException(500, "系统繁忙，请稍后重试");
        }
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException(404, "订单不存在");
            }
            
            // 2. 验证订单状态是否可以拒绝
            int currentStatus = order.getOrderStatus().intValue();
            if (currentStatus != OrderStatusEnum.PAID.getCode()) {
                throw new BusinessException(400, "只有已付款的订单可以拒绝");
            }
            
            // 3. 更新订单状态
            order.setOrderStatus(BigInteger.valueOf(OrderStatusEnum.REFUNDING.getCode()));
            int result = orderMapper.updateById(order);
            if (result <= 0) {
                logger.error("拒绝订单失败，orderId: {}", orderId);
                throw new BusinessException(500, "拒绝订单失败，请稍后重试");
            }
            
            logger.info("订单已拒绝，orderId: {}", orderId);
            return R.success("订单已拒绝");
        } finally {
            // 释放锁
            redisTemplate.delete(orderLockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R markAsPreparing(BigInteger orderId) {
        String orderLockKey = ORDER_LOCK_PREFIX + orderId;
        if (!tryLock(orderLockKey, LOCK_EXPIRATION)) {
            throw new BusinessException(500, "系统繁忙，请稍后重试");
        }
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException(404, "订单不存在");
            }
            
            // 2. 验证订单状态是否可以标记为出餐
            int currentStatus = order.getOrderStatus().intValue();
            if (currentStatus != OrderStatusEnum.PAID.getCode() && currentStatus != OrderStatusEnum.PREPARING.getCode()) {
                throw new BusinessException(400, "只有已付款或待出餐的订单可以标记为出餐");
            }
            
            // 3. 更新订单状态
            order.setOrderStatus(BigInteger.valueOf(OrderStatusEnum.PREPARING.getCode()));
            int result = orderMapper.updateById(order);
            if (result <= 0) {
                logger.error("标记出餐失败，orderId: {}", orderId);
                throw new BusinessException(500, "标记出餐失败，请稍后重试");
            }
            
            logger.info("订单已标记为出餐，orderId: {}", orderId);
            return R.success("订单已标记为出餐");
        } finally {
            // 释放锁
            redisTemplate.delete(orderLockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R markAsDelivering(BigInteger orderId) {
        String orderLockKey = ORDER_LOCK_PREFIX + orderId;
        if (!tryLock(orderLockKey, LOCK_EXPIRATION)) {
            throw new BusinessException(500, "系统繁忙，请稍后重试");
        }
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException(404, "订单不存在");
            }
            
            // 2. 验证订单状态是否可以标记为配送中
            int currentStatus = order.getOrderStatus().intValue();
            if (currentStatus != OrderStatusEnum.PREPARING.getCode()) {
                throw new BusinessException(400, "只有待出餐的订单可以标记为配送中");
            }
            
            // 3. 更新订单状态
            order.setOrderStatus(BigInteger.valueOf(OrderStatusEnum.DELIVERING.getCode()));
            int result = orderMapper.updateById(order);
            if (result <= 0) {
                logger.error("标记配送中失败，orderId: {}", orderId);
                throw new BusinessException(500, "标记配送中失败，请稍后重试");
            }
            
            logger.info("订单已标记为配送中，orderId: {}", orderId);
            return R.success("订单已标记为配送中");
        } finally {
            // 释放锁
            redisTemplate.delete(orderLockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R markAsReadyForPickup(BigInteger orderId) {
        String orderLockKey = ORDER_LOCK_PREFIX + orderId;
        if (!tryLock(orderLockKey, LOCK_EXPIRATION)) {
            throw new BusinessException(500, "系统繁忙，请稍后重试");
        }
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException(404, "订单不存在");
            }
            
            // 2. 验证订单状态是否可以标记为待取餐
            int currentStatus = order.getOrderStatus().intValue();
            if (currentStatus != OrderStatusEnum.DELIVERING.getCode()) {
                throw new BusinessException(400, "只有配送中的订单可以标记为待取餐");
            }
            
            // 3. 更新订单状态
            order.setOrderStatus(BigInteger.valueOf(OrderStatusEnum.READY_FOR_PICKUP.getCode()));
            int result = orderMapper.updateById(order);
            if (result <= 0) {
                logger.error("标记待取餐失败，orderId: {}", orderId);
                throw new BusinessException(500, "标记待取餐失败，请稍后重试");
            }
            
            logger.info("订单已标记为待取餐，orderId: {}", orderId);
            return R.success("订单已标记为待取餐");
        } finally {
            // 释放锁
            redisTemplate.delete(orderLockKey);
        }
    }
    
    /**
     * 保存订单商品关联关系
     */
    private void saveOrderDishRelations(BigInteger orderId, List<OrderItemDTO> items) {
        for (OrderItemDTO item : items) {
            OrderDishRelation relation = new OrderDishRelation();
            relation.setDishId(item.getDishId());
            relation.setOrderId(orderId);
            orderDishRelationMapper.insert(relation);
        }
    }
    
    /**
     * 获取当前登录用户的ID
     */
    private BigInteger getCurrentUserId() {
        Long userIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return BigInteger.valueOf(userIdLong);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNum() {
        return System.currentTimeMillis() + "" + (int) (Math.random() * 10000);
    }

    /**
     * 尝试获取分布式锁
     */
    private boolean tryLock(String lockKey, int expireTime) {
        String requestId = UUID.randomUUID().toString();
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
        return result != null && result;
    }
}
