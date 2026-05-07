package com.example.orderfood.demos.web.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {//传输用户登录数据
    private String userAccount;
    private String password;
}
