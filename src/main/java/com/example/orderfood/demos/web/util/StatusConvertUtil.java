package com.example.orderfood.demos.web.util;

import com.example.orderfood.demos.web.enums.CategoryStatusEnum;
import com.example.orderfood.demos.web.enums.DishStatusEnum;
import com.example.orderfood.demos.web.enums.OrderStatusEnum;
import com.example.orderfood.demos.web.enums.ShopStatusEnum;
import com.example.orderfood.demos.web.enums.UserStatusEnum;

/**
 * 状态转换工具类
 * 用于在数字状态和枚举状态之间进行转换
 */
public class StatusConvertUtil {
    
    // ========== 订单状态转换 ==========
    
    /**
     * 将数字订单状态转换为枚举
     */
    public static OrderStatusEnum convertToOrderStatus(int status) {
        return OrderStatusEnum.fromCode(status);
    }
    
    /**
     * 将订单状态枚举转换为数字
     */
    public static int convertToOrderStatusCode(OrderStatusEnum statusEnum) {
        return statusEnum.getCode();
    }
    
    /**
     * 获取订单状态描述
     */
    public static String getOrderStatusDescription(int status) {
        return OrderStatusEnum.getDescriptionByCode(status);
    }
    
    // ========== 商品分类状态转换 ==========
    
    /**
     * 将数字商品分类状态转换为枚举
     */
    public static CategoryStatusEnum convertToCategoryStatus(int status) {
        return CategoryStatusEnum.fromCode(status);
    }
    
    /**
     * 将商品分类状态枚举转换为数字
     */
    public static int convertToCategoryStatusCode(CategoryStatusEnum statusEnum) {
        return statusEnum.getCode();
    }
    
    /**
     * 获取商品分类状态描述
     */
    public static String getCategoryStatusDescription(int status) {
        return CategoryStatusEnum.getDescriptionByCode(status);
    }
    
    // ========== 食堂店铺状态转换 ==========
    
    /**
     * 将数字店铺状态转换为枚举
     */
    public static ShopStatusEnum convertToShopStatus(int status) {
        return ShopStatusEnum.fromCode(status);
    }
    
    /**
     * 将店铺状态枚举转换为数字
     */
    public static int convertToShopStatusCode(ShopStatusEnum statusEnum) {
        return statusEnum.getCode();
    }
    
    /**
     * 获取店铺状态描述
     */
    public static String getShopStatusDescription(int status) {
        return ShopStatusEnum.getDescriptionByCode(status);
    }
    
    // ========== 用户状态转换 ==========
    
    /**
     * 将数字用户状态转换为枚举
     */
    public static UserStatusEnum convertToUserStatus(int status) {
        return UserStatusEnum.fromCode(status);
    }
    
    /**
     * 将用户状态枚举转换为数字
     */
    public static int convertToUserStatusCode(UserStatusEnum statusEnum) {
        return statusEnum.getCode();
    }
    
    /**
     * 获取用户状态描述
     */
    public static String getUserStatusDescription(int status) {
        return UserStatusEnum.getDescriptionByCode(status);
    }
    
    // ========== 菜品状态转换 ==========
    
    /**
     * 将数字菜品状态转换为枚举
     */
    public static DishStatusEnum convertToDishStatus(int status) {
        return DishStatusEnum.fromCode(status);
    }
    
    /**
     * 将菜品状态枚举转换为数字
     */
    public static int convertToDishStatusCode(DishStatusEnum statusEnum) {
        return statusEnum.getCode();
    }
    
    /**
     * 获取菜品状态描述
     */
    public static String getDishStatusDescription(int status) {
        return DishStatusEnum.getDescriptionByCode(status);
    }
    
    // ========== 常用状态判断方法 ==========
    
    /**
     * 判断订单是否已完成
     */
    public static boolean isOrderCompleted(int orderStatus) {
        OrderStatusEnum statusEnum = convertToOrderStatus(orderStatus);
        return statusEnum == OrderStatusEnum.EVALUATED || 
               statusEnum == OrderStatusEnum.CANCELLED || 
               statusEnum == OrderStatusEnum.REFUNDED;
    }
    
    /**
     * 判断用户是否正常
     */
    public static boolean isUserNormal(int userStatus) {
        return convertToUserStatus(userStatus) == UserStatusEnum.NORMAL;
    }
    
    /**
     * 判断菜品是否可购买
     */
    public static boolean isDishAvailable(int dishStatus) {
        return convertToDishStatus(dishStatus) == DishStatusEnum.ON_SHELF;
    }
    
    /**
     * 判断店铺是否营业
     */
    public static boolean isShopOpen(int shopStatus) {
        return convertToShopStatus(shopStatus) == ShopStatusEnum.OPEN;
    }
}