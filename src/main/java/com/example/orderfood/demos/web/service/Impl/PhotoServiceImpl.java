package com.example.orderfood.demos.web.service.Impl;

import com.example.orderfood.demos.web.DTO.PhotoInsertDTO;
import com.example.orderfood.demos.web.mapper.PhotoMapper;
import com.example.orderfood.demos.web.model.Photo;
import com.example.orderfood.demos.web.service.PhotoService;
import com.example.orderfood.demos.web.service.strategy.PhotoQueryStrategyManager;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 图片服务实现类
 * 提供图片上传、加载等功能的具体实现
 */
@Service
public class PhotoServiceImpl implements PhotoService {

    @Resource
    private PhotoMapper photoMapper;

    /**
     * 图片查询策略管理器
     */
    @Resource
    private PhotoQueryStrategyManager photoQueryStrategyManager;

    /**
     * 图片上传根路径
     */
    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 允许的图片类型
     */
    @Value("${file.upload.allowed-types}")
    private String allowedTypes;

    /**
     * 日期格式化，用于生成目录结构
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 支持的图片类型列表
     */
    private static final java.util.List<String> SUPPORTED_TYPES =
            java.util.Arrays.asList("activities", "avatars", "comments", "dishes", "shops");

    /**
     * 插入图片记录
     * @param dto 图片插入DTO
     */
    @Override
    public void insertPhoto(PhotoInsertDTO dto) {
        Photo photo = new Photo();
        photo.setCommentId(dto.getCommentId());
        photo.setShopId(dto.getShopId());
        photo.setDishId(dto.getDishId());
        photo.setUserId(dto.getUserId());
        photo.setActivityId(dto.getActivityId());
        photoMapper.insert(photo);
    }

    /**
     * 上传图片
     * @param file 图片文件
     * @param type 图片类型：activities, avatars, comments, dishes, shops
     * @param dto 图片关联信息
     * @return 上传结果，包含图片ID和访问路径
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R uploadImage(MultipartFile file, String type, PhotoInsertDTO dto) {
        try {
            // 1. 验证文件
            validateFile(file);
            
            // 2. 验证图片类型
            validateImageType(type);
            
            // 3. 生成唯一文件名
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            
            // 4. 构建文件保存路径
            String datePath = DATE_FORMAT.format(new Date());
            String relativePath = type + "/" + datePath;
            String fullPath = uploadPath + File.separator + relativePath;
            
            // 5. 创建目录（如果不存在）
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 6. 保存文件
            Path filePath = Paths.get(fullPath, fileName);
            Files.write(filePath, file.getBytes());
            
            // 7. 构建图片访问URL
            String fileUrl = "/" + relativePath + "/" + fileName;
            
            // 8. 保存图片信息到数据库
            Photo photo = new Photo();
            photo.setCommentId(dto.getCommentId());
            photo.setShopId(dto.getShopId());
            photo.setDishId(dto.getDishId());
            photo.setUserId(dto.getUserId());
            photo.setActivityId(dto.getActivityId());
            photo.setUrl(fileUrl);
            
            photoMapper.insert(photo);
            
            // 9. 构建返回结果
            R result = R.success("图片上传成功");
            result.put("photoId", photo.getPhotoId());
            result.put("url", fileUrl);
            
            return result;
            
        } catch (IOException e) {
            throw new RuntimeException("图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 根据图片ID获取图片信息
     * @param photoId 图片ID
     * @return 图片信息
     */
    @Override
    public Photo getPhotoById(BigInteger photoId) {
        return photoMapper.selectById(photoId);
    }

    /**
     * 根据关联ID获取图片列表
     * @param relationId 关联ID
     * @param type 图片类型
     * @return 图片列表
     */
    @Override
    public List<Photo> getPhotosByRelationId(BigInteger relationId, String type) {
        // 使用策略模式替代switch case
        return photoQueryStrategyManager.queryPhotos(type, relationId);
    }

    /**
     * 删除图片
     * @param photoId 图片ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deletePhoto(BigInteger photoId) {
        try {
            // 1. 获取图片信息
            Photo photo = photoMapper.selectById(photoId);
            if (photo == null) {
                return R.error(404, "图片不存在");
            }
            
            // 2. 删除物理文件
            String fullPath = uploadPath + photo.getUrl();
            File file = new File(fullPath);
            if (file.exists()) {
                Files.delete(file.toPath());
            }
            
            // 3. 删除数据库记录
            photoMapper.deleteById(photoId);
            
            return R.success("图片删除成功");
            
        } catch (IOException e) {
            throw new RuntimeException("图片删除失败：" + e.getMessage());
        }
    }

    /**
     * 验证文件
     * @param file 图片文件
     */
    private void validateFile(MultipartFile file) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        //Spring Boot已在application.yml中配置了max-file-size和max-request-size
    }



    /**
     * 验证图片类型
     * @param type 图片类型
     */
    private void validateImageType(String type) {
        // 检查图片类型是否为空
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("图片类型不能为空");
        }
        
        // 检查图片类型是否合法
        if (!SUPPORTED_TYPES.contains(type)) {
            throw new IllegalArgumentException("不支持的图片类型：" + type);
        }
    }

    /**
     * 生成唯一文件名
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    private String generateUniqueFileName(String originalFilename) {
        // 获取文件扩展名
        String extension = StringUtils.getFilenameExtension(originalFilename);
        
        // 生成UUID作为文件名，避免冲突
        String uuid = UUID.randomUUID().toString().replace("-", "");
        
        // 构建新文件名：UUID.扩展名
        return uuid + "." + extension;
    }
}