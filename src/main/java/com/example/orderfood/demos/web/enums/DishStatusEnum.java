package com.example.orderfood.demos.web.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 菜品状态枚举
 */
@Getter
public enum DishStatusEnum {
    
    /**
     * 待审核
     */
    PENDING_REVIEW(0, "待审核"),
    
    /**
     * 已上架
     */
    ON_SHELF(1, "已上架"),
    
    /**
     * 已售罄
     */
    SOLD_OUT(2, "已售罄"),
    
    /**
     * 暂时下架
     */
    TEMPORARILY_OFF_SHELF(3, "暂时下架"),
    
    /**
     * 已删除
     */
    DELETED(4, "已删除"),
    
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
    private static final Map<Integer, DishStatusEnum> CODE_MAP = new HashMap<>();
    
    static {
        for (DishStatusEnum status : values()) {
            CODE_MAP.put(status.code, status);
        }
    }
    
    DishStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态码获取枚举
     */
    public static DishStatusEnum fromCode(int code) {
        return CODE_MAP.get(code);
    }
    
    /**
     * 根据状态码获取描述
     */
    public static String getDescriptionByCode(int code) {
        DishStatusEnum status = fromCode(code);
        return status != null ? status.description : "未知状态";
    }
}