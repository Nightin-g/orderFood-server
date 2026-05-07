package com.example.orderfood.demos.web.mapper;

import com.example.orderfood.demos.web.model.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface DishMapper {
    int insert(Dish dish);
    
    int updateById(Dish dish);
    
    int updateSelective(Dish dish);
    
    int deleteById(@Param("dishId") BigInteger dishId);
    
    Dish selectById(@Param("dishId") BigInteger dishId);
    
    List<Dish> selectAll();
    
    List<Dish> selectByExample(Dish dish);
    
    /**
     * 查询未审核的菜品
     */
    List<Dish> selectPendingReview();
}