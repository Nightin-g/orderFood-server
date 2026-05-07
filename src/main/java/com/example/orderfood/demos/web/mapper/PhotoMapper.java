package com.example.orderfood.demos.web.mapper;

import com.example.orderfood.demos.web.model.Photo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface PhotoMapper {
    int insert(Photo photo);
    
    int updateById(Photo photo);
    
    int updateSelective(Photo photo);
    
    int deleteById(@Param("photoId") BigInteger photoId);
    
    Photo selectById(@Param("photoId") BigInteger photoId);
    
    List<Photo> selectAll();
    
    List<Photo> selectByExample(Photo photo);
    
    List<Photo> selectByCommentId(@Param("commentId") BigInteger commentId);
    
    List<Photo> selectByShopId(@Param("shopId") BigInteger shopId);
    
    List<Photo> selectByDishId(@Param("dishId") BigInteger dishId);
    
    List<Photo> selectByUserId(@Param("userId") BigInteger userId);
    
    List<Photo> selectByActivityId(@Param("activityId") BigInteger activityId);
}