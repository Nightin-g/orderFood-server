package com.example.orderfood.demos.web.mapper;

import com.example.orderfood.demos.web.model.OrderDishRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface OrderDishRelationMapper {
    int insert(OrderDishRelation orderDishRelation);
    
    int deleteById(@Param("odID") BigInteger odID);
    
    OrderDishRelation selectById(@Param("odID") BigInteger odID);
    
    List<OrderDishRelation> selectByOrderId(@Param("orderId") BigInteger orderId);
    
    List<OrderDishRelation> selectByDishId(@Param("dishId") BigInteger dishId);
}
