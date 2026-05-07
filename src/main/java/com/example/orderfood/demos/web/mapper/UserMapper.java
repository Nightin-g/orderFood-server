package com.example.orderfood.demos.web.mapper;

import com.example.orderfood.demos.web.DTO.UserUpdateDTO;
import com.example.orderfood.demos.web.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface UserMapper {
    int insert(User user);
    
    int updateSelective(User user);
    
    int deleteById(@Param("userId") BigInteger userId);
    
    User selectById(@Param("userId") BigInteger userId);
    
    List<User> selectAll();
    
    List<User> selectByExample(User user);
    
    User selectByUserAccount(@Param("userAccount") String userAccount);
    
    int updateUserInfo(User user);

    int updatePassword(UserUpdateDTO dto);
}