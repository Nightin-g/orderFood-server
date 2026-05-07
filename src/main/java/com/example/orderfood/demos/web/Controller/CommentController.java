package com.example.orderfood.demos.web.Controller;

import com.example.orderfood.demos.web.DTO.CommentCreateDTO;
import com.example.orderfood.demos.web.service.CommentService;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

/**
 * 评论控制器
 * 处理评论相关的HTTP请求
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 创建评论
     */
    @PostMapping("/create")
    public R createComment(@RequestBody CommentCreateDTO commentCreateDTO) {
        return commentService.createComment(commentCreateDTO);
    }

    /**
     * 查询店铺的评论
     */
    @GetMapping("/shop/{shopId}")
    public R getShopComments(@PathVariable("shopId") BigInteger shopId) {
        List<?> comments = commentService.getShopComments(shopId);
        return R.success("查询店铺评论成功", comments);
    }

    /**
     * 查询菜品的评论
     */
    @GetMapping("/dish/{dishId}")
    public R getDishComments(@PathVariable("dishId") BigInteger dishId) {
        List<?> comments = commentService.getDishComments(dishId);
        return R.success("查询菜品评论成功", comments);
    }

    /**
     * 查询用户的评论
     */
    @GetMapping("/user")
    public R getUserComments() {
        List<?> comments = commentService.getUserComments();
        return R.success("查询用户评论成功", comments);
    }

    /**
     * 获取店铺的平均评分
     */
    @GetMapping("/shop/{shopId}/score")
    public R getShopAverageScore(@PathVariable("shopId") BigInteger shopId) {
        Object averageScore = commentService.getShopAverageScore(shopId);
        return R.success("获取店铺平均评分成功", averageScore);
    }

    /**
     * 获取菜品的平均评分
     */
    @GetMapping("/dish/{dishId}/score")
    public R getDishAverageScore(@PathVariable("dishId") BigInteger dishId) {
        Object averageScore = commentService.getDishAverageScore(dishId);
        return R.success("获取菜品平均评分成功", averageScore);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/delete/{commentId}")
    public R deleteComment(@PathVariable("commentId") BigInteger commentId) {
        return commentService.deleteComment(commentId);
    }
}
