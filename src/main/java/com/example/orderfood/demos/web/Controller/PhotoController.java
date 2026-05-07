package com.example.orderfood.demos.web.Controller;

import com.example.orderfood.demos.web.DTO.PhotoInsertDTO;
import com.example.orderfood.demos.web.model.Photo;
import com.example.orderfood.demos.web.service.PhotoService;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

/**
 * 图片控制器
 * 处理图片上传、加载、删除等HTTP请求
 */
@RestController
@RequestMapping("/photo")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    /**
     * 通用图片上传接口
     * @param file 图片文件
     * @param dto 图片上传DTO，包含图片类型和关联信息
     * @return 上传结果
     */
    @PostMapping("/upload")
    public R uploadImage(@RequestParam("file") MultipartFile file,
                         PhotoInsertDTO dto) {
        // 从DTO中获取图片类型
        String type = dto.getType();
        return photoService.uploadImage(file, type, dto);
    }

    /**
     * 根据图片ID获取图片信息
     * @param photoId 图片ID
     * @return 图片信息
     */
    @GetMapping("/{photoId}")
    public R getPhotoById(@PathVariable("photoId") BigInteger photoId) {
        Photo photo = photoService.getPhotoById(photoId);
        if (photo == null) {
            return R.error(404, "图片不存在");
        }
        return R.success("获取图片成功", photo);
    }

    /**
     * 根据关联ID获取图片列表
     * @param relationId 关联ID
     * @param type 图片类型：activities, avatars, comments, dishes, shops
     * @return 图片列表
     */
    @GetMapping("/list")
    public R getPhotosByRelationId(@RequestParam("relationId") BigInteger relationId, @RequestParam("type") String type) {
        List<Photo> photos = photoService.getPhotosByRelationId(relationId, type);
        return R.success("获取图片列表成功", photos);
    }

    /**
     * 删除图片
     * @param photoId 图片ID
     * @return 删除结果
     */
    @DeleteMapping("/{photoId}")
    public R deletePhoto(@PathVariable("photoId") BigInteger photoId) {
        return photoService.deletePhoto(photoId);
    }
}
