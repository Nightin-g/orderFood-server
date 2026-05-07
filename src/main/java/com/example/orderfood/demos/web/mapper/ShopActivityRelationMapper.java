package com.example.orderfood.demos.web.mapper;

import com.example.orderfood.demos.web.model.ShopActivityRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface ShopActivityRelationMapper {
    int insert(ShopActivityRelation shopActivityRelation);
    
    int updateById(ShopActivityRelation shopActivityRelation);
    
    int updateSelective(ShopActivityRelation shopActivityRelation);
    
    int deleteById(@Param("saId") BigInteger saId);
    
    ShopActivityRelation selectById(@Param("saId") BigInteger saId);
    
    List<ShopActivityRelation> selectAll();
    
    List<ShopActivityRelation> selectByExample(ShopActivityRelation shopActivityRelation);
    
    List<ShopActivityRelation> selectByShopId(@Param("shopId") BigInteger shopId);
    
    List<ShopActivityRelation> selectByActivityId(@Param("activityId") BigInteger activityId);
}