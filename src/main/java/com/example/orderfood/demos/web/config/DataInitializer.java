package com.example.orderfood.demos.web.config;

import com.example.orderfood.demos.web.mapper.AdministratorMapper;
import com.example.orderfood.demos.web.model.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdministratorMapper administratorMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Administrator existing = administratorMapper.selectByAdminAccount("admin");
        if (existing == null) {
            Administrator admin = new Administrator();
            admin.setAdminName("系统管理员");
            admin.setAdminAccount("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setPhoneNum("13800000000");
            admin.setJurisdiction(0);
            administratorMapper.insert(admin);
            System.out.println(">>> 管理员账号已初始化: admin / 123456");
        }
    }
}
