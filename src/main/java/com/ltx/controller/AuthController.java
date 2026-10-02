package com.ltx.controller;

import cn.hutool.core.util.StrUtil;
import com.ltx.constant.Constant;
import com.ltx.entity.Result;
import com.ltx.entity.SecurityUser;
import com.ltx.entity.User;
import com.ltx.enums.ErrorCode;
import com.ltx.service.UserDetailsServiceImpl;
import com.ltx.util.JwtUtil;
import com.ltx.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 认证控制器
 *
 * @author tianxing
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RedisUtil redisUtil;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * 刷新访问令牌
     *
     * @param request 请求对象
     * @return 通用响应对象
     */
    @PostMapping("/refresh")
    public Result refresh(HttpServletRequest request) {
        // 从请求头中获取RefreshToken
        String refreshToken = request.getHeader(Constant.REFRESH_TOKEN);
        if (StrUtil.isBlank(refreshToken)) {
            return Result.fail(ErrorCode.REFRESH_TOKEN_IS_NULL);
        }
        // 根据RefreshToken从Redis中获取用户名
        String key = Constant.REFRESH_TOKEN_KEY + refreshToken;
        String username = redisUtil.get(key);
        if (StrUtil.isBlank(username)) {
            return Result.fail(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        // 根据用户名获取用户信息与权限
        SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(username);
        User user = securityUser.getUser();
        List<String> authorities = securityUser.getAuthorities().stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();
        // 签发新的AccessToken
        String accessToken = JwtUtil.createJws(user, authorities, Constant.ACCESS_TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
        // 重置RefreshToken过期时间
        redisUtil.expire(key, Constant.REFRESH_TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return Result.success("刷新成功").put(Constant.ACCESS_TOKEN, accessToken);
    }
}
