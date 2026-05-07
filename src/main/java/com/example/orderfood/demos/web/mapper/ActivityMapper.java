package com.example.orderfood.demos.web.mapper;

import com.example.orderfood.demos.web.model.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface ActivityMapper {
    int insert(Activity activity);
    
    int updateById(Activity activity);
    
    int updateSelective(Activity activity);
    
    int deleteById(@Param("activityId") BigInteger activityId);
    
    Activity selectById(@Param("activityId") BigInteger activityId);
    
    List<Activity> selectAll();
    
    List<Activity> selectByExample(Activity activity);
}