package com.example.orderfood.demos.web.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应体
 */
public class R {
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 返回消息
     */
    private String msg;
    
    /**
     * 返回数据
     */
    private Object data;
    
    /**
     * 额外参数
     */
    private Map<String, Object> extra;
    
    /**
     * 私有构造方法
     */
    private R() {
        this.extra = new HashMap<>();
    }
    
    /**
     * 构造方法
     * @param code 状态码
     * @param msg 返回消息
     * @param data 返回数据
     */
    private R(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.extra = new HashMap<>();
    }
    
    /**
     * 成功响应 - 无数据
     * @return R
     */
    public static R success() {
        return new R(HttpStatus.SUCCESS, "操作成功", null);
    }
    
    /**
     * 成功响应 - 带消息
     * @param msg 返回消息
     * @return R
     */
    public static R success(String msg) {
        return new R(HttpStatus.SUCCESS, msg, null);
    }
    
    /**
     * 成功响应 - 带数据
     * @param data 返回数据
     * @return R
     */
    public static R success(Object data) {
        return new R(HttpStatus.SUCCESS, "操作成功", data);
    }
    
    /**
     * 成功响应 - 带消息和数据
     * @param msg 返回消息
     * @param data 返回数据
     * @return R
     */
    public static R success(String msg, Object data) {
        return new R(HttpStatus.SUCCESS, msg, data);
    }
    
    /**
     * 失败响应 - 默认
     * @return R
     */
    public static R error() {
        return new R(HttpStatus.ERROR, "操作失败", null);
    }
    
    /**
     * 失败响应 - 带消息
     * @param msg 返回消息
     * @return R
     */
    public static R error(String msg) {
        return new R(HttpStatus.ERROR, msg, null);
    }
    
    /**
     * 失败响应 - 带消息和数据
     * @param msg 返回消息
     * @param data 返回数据
     * @return R
     */
    public static R error(String msg, Object data) {
        return new R(HttpStatus.ERROR, msg, data);
    }
    
    /**
     * 失败响应 - 带状态码和消息
     * @param code 状态码
     * @param msg 返回消息
     * @return R
     */
    public static R error(Integer code, String msg) {
        return new R(code, msg, null);
    }
    
    /**
     * 失败响应 - 完整参数
     * @param code 状态码
     * @param msg 返回消息
     * @param data 返回数据
     * @return R
     */
    public static R error(Integer code, String msg, Object data) {
        return new R(code, msg, data);
    }
    
    /**
     * 添加额外参数
     * @param key 键
     * @param value 值
     * @return R
     */
    public R put(String key, Object value) {
        this.extra.put(key, value);
        return this;
    }
    
    /**
     * 添加多个额外参数
     * @param map 额外参数Map
     * @return R
     */
    public R putAll(Map<String, Object> map) {
        this.extra.putAll(map);
        return this;
    }
    
    // getter and setter methods
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public Map<String, Object> getExtra() {
        return extra;
    }
    
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
