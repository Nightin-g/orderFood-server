package com.example.orderfood.demos.web.mapper;

import com.example.orderfood.demos.web.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface OrderMapper {
    int insert(Order order);
    
    int updateById(Order order);
    
    int updateSelective(Order order);
    
    int deleteById(@Param("orderId") BigInteger orderId);
    
    Order selectById(@Param("orderId") BigInteger orderId);
    
    List<Order> selectAll();
    
    List<Order> selectByExample(Order order);
}