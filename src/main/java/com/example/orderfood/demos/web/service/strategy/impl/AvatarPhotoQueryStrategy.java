package com.example.orderfood.demos.web.service.strategy.impl;

import com.example.orderfood.demos.web.mapper.PhotoMapper;
import com.example.orderfood.demos.web.model.Photo;
import com.example.orderfood.demos.web.service.strategy.PhotoQueryStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

/**
 * 头像图片查询策略
 */
@Component
public class AvatarPhotoQueryStrategy implements PhotoQueryStrategy {

    @Resource
    private PhotoMapper photoMapper;

    @Override
    public List<Photo> queryPhotos(BigInteger relationId) {
        return photoMapper.selectByUserId(relationId);
    }

    @Override
    public String getType() {
        return "avatars";
    }
}