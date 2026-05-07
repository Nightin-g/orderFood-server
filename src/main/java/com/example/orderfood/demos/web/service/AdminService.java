package com.example.orderfood.demos.web.service;

import com.example.orderfood.demos.web.DTO.AdminLoginDTO;
import com.example.orderfood.demos.web.util.R;

public interface AdminService {
    R adminLogin(AdminLoginDTO adminLoginDTO);
}
