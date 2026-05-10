package com.example.orderfood.demos.web.mapper;

import com.example.orderfood.demos.web.model.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface ShopMapper {
    int insert(Shop shop);
    
    int updateById(Shop shop);
    
    int updateSelective(Shop shop);
    
    int deleteById(@Param("shopId") BigInteger shopId);
    
    Shop selectById(@Param("shopId") BigInteger shopId);
    
    List<Shop> selectAll();
    
    List<Shop> selectByExample(Shop shop);
    
    /**
     * 查询未审核的店铺
     */
    List<Shop> selectPendingReview();
    
    /**
     * 根据店铺账号查询店铺
     */
    Shop selectByShopAccount(@Param("shopAccount") String shopAccount);

    /**
     * 查询所有已审核通过的店铺（非待审核、非审核未通过）
     */
    List<Shop> selectApproved();
}