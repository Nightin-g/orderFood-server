package com.example.orderfood.demos.web.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单状态枚举
 */

@Getter
public enum OrderStatusEnum {
    
    /**
     * 待付款
     */
    PENDING_PAYMENT(0, "待付款"),
    
    /**
     * 已付款
     */
    PAID(1, "已付款"),
    
    /**
     * 待出餐
     */
    PREPARING(2, "待出餐"),
    
    /**
     * 配送中
     */
    DELIVERING(3, "配送中"),
    
    /**
     * 待取餐
     */
    READY_FOR_PICKUP(4, "待取餐"),
    
    /**
     * 已取餐，未评价
     */
    PICKED_UP_UNEVALUATED(5, "已取餐，未评价"),
    
    /**
     * 已评价
     */
    EVALUATED(6, "已评价"),
    
    /**
     * 已取消
     */
    CANCELLED(7, "已取消"),
    
    /**
     * 退款中
     */
    REFUNDING(8, "退款中"),
    
    /**
     * 已退款
     */
    REFUNDED(9, "已退款");

    /**
     * -- GETTER --
     *  获取状态码
     */
    private final int code;
    /**
     * -- GETTER --
     *  获取状态描述
     */
    private final String description;
    
    // 用于快速查找
    private static final Map<Integer, OrderStatusEnum> CODE_MAP = new HashMap<>();
    
    static {
        for (OrderStatusEnum status : values()) {
            CODE_MAP.put(status.code, status);
        }
    }
    
    OrderStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态码获取枚举
     */
    public static OrderStatusEnum fromCode(int code) {
        return CODE_MAP.get(code);
    }
    
    /**
     * 根据状态码获取描述
     */
    public static String getDescriptionByCode(int code) {
        OrderStatusEnum status = fromCode(code);
        return status != null ? status.description : "未知状态";
    }
}