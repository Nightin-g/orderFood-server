package com.example.orderfood.demos.web.service;

import com.example.orderfood.demos.web.DTO.DishInsertDTO;
import com.example.orderfood.demos.web.model.Dish;
import com.example.orderfood.demos.web.util.R;

import java.math.BigInteger;
import java.util.List;

public interface DishService {
    /**
     * 查询未审核的菜品
     */
    List<Dish> getPendingReviewDishes();

    /**
     * 审核菜品通过
     */
    R approveDish(BigInteger dishId);

    /**
     * 审核菜品不通过
     */
    R rejectDish(BigInteger dishId);

    /**
     * 店铺添加菜品
     */
    R addDish(DishInsertDTO dishInsertDTO);

    /**
     * 根据店铺ID查询菜品列表
     */
    List<Dish> getDishesByShopId(BigInteger shopId);

    /**
     * 根据分类ID查询菜品列表
     */
    List<Dish> getDishesByCategoryId(BigInteger categoryId);

    /**
     * 查询所有上架的菜品
     */
    List<Dish> getOnShelfDishes();

    /**
     * 根据菜品ID查询菜品
     */
    Dish getDishById(BigInteger dishId);

    /**
     * 更新菜品信息
     */
    R updateDish(Dish dish);

    /**
     * 店铺更新自家菜品状态
     */
    R updateDishStatus(BigInteger dishId, Integer status);

    /**
     * 删除菜品
     */
    R deleteDish(BigInteger dishId);

    /**
     * 管理员查询所有菜品
     */
    List<Dish> getAllDishes();
}