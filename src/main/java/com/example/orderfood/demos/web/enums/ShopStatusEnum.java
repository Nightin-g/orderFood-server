package com.example.orderfood.demos.web.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 食堂店铺状态枚举
 */
@Getter
public enum ShopStatusEnum {
    
    /**
     * 待审核
     */
    PENDING_REVIEW(0, "待审核"),
    
    /**
     * 正在营业
     */
    OPEN(1, "正在营业"),
    
    /**
     * 休息中
     */
    RESTING(2, "休息中"),
    
    /**
     * 暂时歇业
     */
    TEMPORARILY_CLOSED(3, "暂时歇业"),
    
    /**
     * 永久停业
     */
    PERMANENTLY_CLOSED(4, "永久停业"),
    
    /**
     * 审核未通过
     */
    REVIEW_FAILED(5, "审核未通过");

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
    private static final Map<Integer, ShopStatusEnum> CODE_MAP = new HashMap<>();
    
    static {
        for (ShopStatusEnum status : values()) {
            CODE_MAP.put(status.code, status);
        }
    }
    
    ShopStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态码获取枚举
     */
    public static ShopStatusEnum fromCode(int code) {
        return CODE_MAP.get(code);
    }
    
    /**
     * 根据状态码获取描述
     */
    public static String getDescriptionByCode(int code) {
        ShopStatusEnum status = fromCode(code);
        return status != null ? status.description : "未知状态";
    }
}