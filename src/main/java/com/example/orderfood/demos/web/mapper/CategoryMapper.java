package com.example.orderfood.demos.web.mapper;

import com.example.orderfood.demos.web.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface CategoryMapper {
    int insert(Category category);
    
    int updateById(Category category);
    
    int updateSelective(Category category);
    
    int deleteById(@Param("categoryId") BigInteger categoryId);
    
    Category selectById(@Param("categoryId") BigInteger categoryId);
    
    List<Category> selectAll();
    
    List<Category> selectByExample(Category category);
}