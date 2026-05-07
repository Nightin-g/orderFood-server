package com.example.orderfood.demos.web.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户状态枚举
 */
@Getter
public enum UserStatusEnum {
    
    /**
     * 正常
     */
    NORMAL(0, "正常"),
    
    /**
     * 受限
     */
    RESTRICTED(1, "受限"),
    
    /**
     * 注销
     */
    CANCELLED(2, "注销");

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
    private static final Map<Integer, UserStatusEnum> CODE_MAP = new HashMap<>();
    
    static {
        for (UserStatusEnum status : values()) {
            CODE_MAP.put(status.code, status);
        }
    }
    
    UserStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态码获取枚举
     */
    public static UserStatusEnum fromCode(int code) {
        return CODE_MAP.get(code);
    }
    
    /**
     * 根据状态码获取描述
     */
    public static String getDescriptionByCode(int code) {
        UserStatusEnum status = fromCode(code);
        return status != null ? status.description : "未知状态";
    }
}