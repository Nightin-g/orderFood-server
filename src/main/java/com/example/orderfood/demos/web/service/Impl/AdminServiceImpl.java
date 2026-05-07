package com.example.orderfood.demos.web.service.Impl;

import com.example.orderfood.demos.web.DTO.AdminLoginDTO;
import com.example.orderfood.demos.web.mapper.AdministratorMapper;
import com.example.orderfood.demos.web.model.Administrator;
import com.example.orderfood.demos.web.service.AdminService;
import com.example.orderfood.demos.web.util.JwtUtils;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdministratorMapper administratorMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public R adminLogin(AdminLoginDTO adminLoginDTO) {
        Administrator admin = administratorMapper.selectByAdminAccount(adminLoginDTO.getAdminAccount());

        if (admin == null) {
            return R.error(401, "管理员不存在");
        }

        if (!passwordEncoder.matches(adminLoginDTO.getPassword(), admin.getPassword())) {
            return R.error(401, "密码错误");
        }

        String token = jwtUtils.generateToken(admin.getAdminId().longValue(), admin.getAdminAccount(), "admin");

        String redisKey = "admin:token:" + admin.getAdminId();
        redisTemplate.opsForValue().set(redisKey, token, 3600, TimeUnit.SECONDS);

        return R.success("登录成功").put("token", token).put("role", "admin");
    }

    @Override
    public R getCurrentAdmin() {
        Long adminIdLong = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BigInteger adminId = BigInteger.valueOf(adminIdLong);

        Administrator admin = administratorMapper.selectById(adminId);
        if (admin == null) {
            return R.error(404, "管理员不存在");
        }

        admin.setPassword(null);
        return R.success(admin);
    }
}
