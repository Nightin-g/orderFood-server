package com.example.orderfood.demos.web.service;

import com.example.orderfood.demos.web.DTO.OrderCreateDTO;
import com.example.orderfood.demos.web.model.Order;
import com.example.orderfood.demos.web.util.R;

import java.math.BigInteger;
import java.util.List;

public interface OrderService {
    /**
     * 创建订单
     */
    R createOrder(OrderCreateDTO orderCreateDTO);
    
    /**
     * 查询用户的所有订单
     */
    List<Order> getUserOrders();
    
    /**
     * 查询订单详情
     */
    R getOrderDetail(BigInteger orderId);
    
    /**
     * 取消订单
     */
    R cancelOrder(BigInteger orderId);
    
    /**
     * 支付订单
     */
    R payOrder(BigInteger orderId);
    
    /**
     * 确认取餐
     */
    R confirmPickup(BigInteger orderId);
    
    /**
     * 评价订单
     */
    R evaluateOrder(BigInteger orderId, Integer score, String comment);
    
    /**
     * 申请退款
     */
    R applyRefund(BigInteger orderId, String reason);
    
    /**
     * 查询店铺的所有订单
     */
    List<Order> getShopOrders();
    
    /**
     * 店铺接单
     */
    R acceptOrder(BigInteger orderId);
    
    /**
     * 店铺拒绝订单
     */
    R rejectOrder(BigInteger orderId);
    
    /**
     * 店铺标记为出餐
     */
    R markAsPreparing(BigInteger orderId);
    
    /**
     * 店铺标记为配送中
     */
    R markAsDelivering(BigInteger orderId);
    
    /**
     * 店铺标记为待取餐
     */
    R markAsReadyForPickup(BigInteger orderId);
}
