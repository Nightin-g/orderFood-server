package com.example.orderfood;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtFilterTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 测试没有令牌时可以访问登录接口
     */
    @Test
    public void testLoginWithoutToken() throws Exception {
        // 构造登录请求体
        String loginJson = "{\"userAccount\": \"test\", \"password\": \"123456\"}";
        
        // 发送POST请求到登录接口，不携带令牌
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk()); // 期望返回200 OK，因为登录接口允许公开访问
    }

    /**
     * 测试没有令牌时可以访问注册接口
     */
    @Test
    public void testRegisterWithoutToken() throws Exception {
        // 构造注册请求体
        String registerJson = "{\"userAccount\": \"test2\", \"password\": \"123456\", \"confirmPassword\": \"123456\"}";
        
        // 发送POST请求到注册接口，不携带令牌
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isOk()); // 期望返回200 OK，因为注册接口允许公开访问
    }

    /**
     * 测试没有令牌时不能访问需要认证的接口
     */
    @Test
    public void testUpdateWithoutToken() throws Exception {
        // 构造更新请求体
        String updateJson = "{\"userName\": \"test\", \"sex\": 1, \"address\": \"test address\"}";
        
        // 发送PUT请求到更新接口，不携带令牌
        mockMvc.perform(put("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isUnauthorized()); // 期望返回401 Unauthorized，因为更新接口需要认证
    }
}
