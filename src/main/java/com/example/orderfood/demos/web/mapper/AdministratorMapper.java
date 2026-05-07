package com.example.orderfood.demos.web.mapper;

import com.example.orderfood.demos.web.model.Administrator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface AdministratorMapper {
    int insert(Administrator administrator);
    
    int updateById(Administrator administrator);
    
    int updateSelective(Administrator administrator);
    
    int deleteById(@Param("adminId") BigInteger adminId);
    
    Administrator selectById(@Param("adminId") BigInteger adminId);
    
    List<Administrator> selectAll();
    
    List<Administrator> selectByExample(Administrator administrator);
}