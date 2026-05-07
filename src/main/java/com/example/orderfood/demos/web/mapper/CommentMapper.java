package com.example.orderfood.demos.web.mapper;

import com.example.orderfood.demos.web.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Mapper
public interface CommentMapper {
    int insert(Comment comment);
    
    int deleteById(@Param("commentId") BigInteger commentId);
    
    Comment selectById(@Param("commentId") BigInteger commentId);
    
    List<Comment> selectByShopId(@Param("shopId") BigInteger shopId);
    
    List<Comment> selectByDishId(@Param("dishId") BigInteger dishId);
    
    List<Comment> selectByUserId(@Param("userId") BigInteger userId);
    
    BigDecimal selectAverageScoreByShopId(@Param("shopId") BigInteger shopId);
    
    BigDecimal selectAverageScoreByDishId(@Param("dishId") BigInteger dishId);
}
