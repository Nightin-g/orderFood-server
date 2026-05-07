package com.example.orderfood.demos.web.service;

import com.example.orderfood.demos.web.DTO.PhotoInsertDTO;
import com.example.orderfood.demos.web.model.Photo;
import com.example.orderfood.demos.web.util.R;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

/**
 * 图片服务接口
 * 提供图片上传、加载等功能
 */
public interface PhotoService {
    
    /**
     * 插入图片记录
     * @param dto 图片插入DTO
     */
    void insertPhoto(PhotoInsertDTO dto);
    
    /**
     * 上传图片
     * @param file 图片文件
     * @param type 图片类型：activities, avatars, comments, dishes, shops
     * @param dto 图片关联信息
     * @return 上传结果，包含图片ID和访问路径
     */
    R uploadImage(MultipartFile file, String type, PhotoInsertDTO dto);
    
    /**
     * 根据图片ID获取图片信息
     * @param photoId 图片ID
     * @return 图片信息
     */
    Photo getPhotoById(BigInteger photoId);
    
    /**
     * 根据关联ID获取图片列表
     * @param relationId 关联ID
     * @param type 图片类型
     * @return 图片列表
     */
    List<Photo> getPhotosByRelationId(BigInteger relationId, String type);
    
    /**
     * 删除图片
     * @param photoId 图片ID
     * @return 删除结果
     */
    R deletePhoto(BigInteger photoId);
}
