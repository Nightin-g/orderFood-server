package com.example.orderfood.demos.web.service.strategy;

import com.example.orderfood.demos.web.model.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片查询策略管理器
 * 管理所有图片查询策略，提供策略获取和执行方法
 */
@Component
public class PhotoQueryStrategyManager {

    /**
     * 所有图片查询策略列表，Spring会自动注入所有实现PhotoQueryStrategy接口的Bean
     */
    @Autowired
    private List<PhotoQueryStrategy> photoQueryStrategies;

    /**
     * 策略映射，key为图片类型，value为对应的策略实现
     */
    private Map<String, PhotoQueryStrategy> strategyMap;

    /**
     * 初始化策略映射
     * 在所有Bean注入完成后执行
     */
    @PostConstruct
    public void init() {
        strategyMap = new HashMap<>();
        for (PhotoQueryStrategy strategy : photoQueryStrategies) {
            strategyMap.put(strategy.getType(), strategy);
        }
    }

    /**
     * 根据图片类型获取对应的查询策略
     * @param type 图片类型
     * @return 图片查询策略
     */
    public PhotoQueryStrategy getStrategy(String type) {
        PhotoQueryStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的图片类型：" + type);
        }
        return strategy;
    }

    /**
     * 根据图片类型和关联ID查询图片列表
     * @param type 图片类型
     * @param relationId 关联ID
     * @return 图片列表
     */
    public List<Photo> queryPhotos(String type, BigInteger relationId) {
        PhotoQueryStrategy strategy = getStrategy(type);
        return strategy.queryPhotos(relationId);
    }
}