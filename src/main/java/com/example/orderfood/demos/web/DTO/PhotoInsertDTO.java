package com.example.orderfood.demos.web.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

/**
 * 图片上传DTO
 * 用于接收图片上传请求的参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoInsertDTO {
    
    /**
     * 图片类型：activities, avatars, comments, dishes, shops
     */
    private String type;
    
    /**
     * 关联的评论ID（可选）
     */
    private BigInteger commentId;
    
    /**
     * 关联的店铺ID（可选）
     */
    private BigInteger shopId;
    
    /**
     * 关联的菜品ID（可选）
     */
    private BigInteger dishId;
    
    /**
     * 关联的用户ID（可选）
     */
    private BigInteger userId;
    
    /**
     * 关联的活动ID（可选）
     */
    private BigInteger activityId;
}
