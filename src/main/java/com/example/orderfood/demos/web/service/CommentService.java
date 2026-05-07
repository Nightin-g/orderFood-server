package com.example.orderfood.demos.web.service;

import com.example.orderfood.demos.web.DTO.CommentCreateDTO;
import com.example.orderfood.demos.web.model.Comment;
import com.example.orderfood.demos.web.util.R;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface CommentService {
    /**
     * 创建评论
     */
    R createComment(CommentCreateDTO commentCreateDTO);
    
    /**
     * 查询店铺的评论
     */
    List<Comment> getShopComments(BigInteger shopId);
    
    /**
     * 查询菜品的评论
     */
    List<Comment> getDishComments(BigInteger dishId);
    
    /**
     * 查询用户的评论
     */
    List<Comment> getUserComments();
    
    /**
     * 获取店铺的平均评分
     */
    BigDecimal getShopAverageScore(BigInteger shopId);
    
    /**
     * 获取菜品的平均评分
     */
    BigDecimal getDishAverageScore(BigInteger dishId);
    
    /**
     * 删除评论
     */
    R deleteComment(BigInteger commentId);
}
