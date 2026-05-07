package com.example.orderfood.demos.web.service;

import com.example.orderfood.demos.web.DTO.UserLoginDTO;
import com.example.orderfood.demos.web.DTO.UserRegisterDTO;
import com.example.orderfood.demos.web.DTO.UserUpdateDTO;
import com.example.orderfood.demos.web.util.R;

public interface UserService
{
    R userLogin(UserLoginDTO userLoginDTO);

    R userRegister(UserRegisterDTO userRegisterDTO);
    
    R updateUserInfo(UserUpdateDTO userUpdateDTO);

    R updatePassword(UserUpdateDTO userUpdateDTO);

    R getCurrentUser();
}
