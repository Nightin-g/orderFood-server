package com.example.orderfood.demos.web.Controller;

import com.example.orderfood.demos.web.DTO.UserLoginDTO;
import com.example.orderfood.demos.web.DTO.UserRegisterDTO;
import com.example.orderfood.demos.web.DTO.UserUpdateDTO;
import com.example.orderfood.demos.web.service.UserService;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public R getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PostMapping("/login")
    public R userLogin(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.userLogin(userLoginDTO);
    }

    @PostMapping("/register")
    public R userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.userRegister(userRegisterDTO);
    }
    
    @PutMapping("/update")
    public R updateUserInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.updateUserInfo(userUpdateDTO);
    }

    @PostMapping("/updatePwd")
    public R updatePwd(@RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.updatePassword(userUpdateDTO);
    }
}
