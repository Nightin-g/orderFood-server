package com.example.orderfood.demos.web.service.strategy;

import com.example.orderfood.demos.web.model.Photo;

import java.math.BigInteger;
import java.util.List;

/**
 * 图片查询策略接口
 * 定义不同类型图片的查询策略
 */
public interface PhotoQueryStrategy {
    
    /**
     * 根据关联ID查询图片列表
     * @param relationId 关联ID
     * @return 图片列表
     */
    List<Photo> queryPhotos(BigInteger relationId);
    
    /**
     * 获取策略支持的图片类型
     * @return 图片类型
     */
    String getType();
}