package com.example.orderfood.demos.web.config;

import com.example.orderfood.demos.web.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 1. 从请求头中获取Authorization字段
        String token = getTokenFromRequest(request);
        
        // 2. 验证令牌
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            // 3. 从令牌中获取用户/店铺ID
            Long id = jwtUtils.getUserIdFromToken(token);
            String account = jwtUtils.getUserAccountFromToken(token);
            
            // 4. 验证令牌是否在Redis中有效
            // 尝试用户令牌
            String userRedisKey = "user:token:" + id;
            String userRedisToken = redisTemplate.opsForValue().get(userRedisKey);
            
            // 尝试店铺令牌
            String shopRedisKey = "shop:token:" + id;
            String shopRedisToken = redisTemplate.opsForValue().get(shopRedisKey);
            
            if (token.equals(userRedisToken) || token.equals(shopRedisToken)) {
                // 5. 设置认证信息（这里简化处理，实际项目中应该从数据库获取用户/店铺权限信息）
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(id, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        chain.doFilter(request, response);
    }
    
    /**
     * 从请求头中获取令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}