package com.example.orderfood.demos.web.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品分类状态枚举
 */

@Getter
public enum CategoryStatusEnum {
    
    /**
     * 未被使用
     */
    NOT_USED(0, "未被使用"),
    
    /**
     * 正在使用
     */
    IN_USE(1, "正在使用"),
    
    /**
     * 暂时停用
     */
    TEMPORARILY_DISABLED(2, "暂时停用");

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
    private static final Map<Integer, CategoryStatusEnum> CODE_MAP = new HashMap<>();
    
    static {
        for (CategoryStatusEnum status : values()) {
            CODE_MAP.put(status.code, status);
        }
    }
    
    CategoryStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态码获取枚举
     */
    public static CategoryStatusEnum fromCode(int code) {
        return CODE_MAP.get(code);
    }
    
    /**
     * 根据状态码获取描述
     */
    public static String getDescriptionByCode(int code) {
        CategoryStatusEnum status = fromCode(code);
        return status != null ? status.description : "未知状态";
    }
}