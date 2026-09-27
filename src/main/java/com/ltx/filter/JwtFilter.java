package com.ltx.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.ltx.constant.Constant;
import com.ltx.entity.Result;
import com.ltx.entity.User;
import com.ltx.enums.ErrorCode;
import com.ltx.util.JwtUtil;
import com.ltx.util.ServletUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Jwt过滤器
 *
 * @author tianxing
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    /**
     * 判断当前请求是否放行
     *
     * @param request 请求对象
     * @return 是否放行请求
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String uri = request.getRequestURI();
        return "/login".equals(uri) || "/auth/refresh".equals(uri);
    }

    /**
     * 对每个请求执行一次过滤操作
     *
     * @param request  请求对象{@link HttpServletRequest}
     * @param response 响应对象{@link HttpServletResponse}
     * @param chain    过滤器链
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        // 从请求头中获取AccessToken
        String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
        // 如果AccessToken为空
        if (StrUtil.isBlank(accessToken)) {
            ServletUtil.write(response, Result.fail(ErrorCode.ACCESS_TOKEN_IS_NULL));
            return;
        }
        // 校验并解析AccessToken
        Claims claims;
        try {
            claims = JwtUtil.getPayLoad(accessToken);
        } catch (ExpiredJwtException e) {
            ServletUtil.write(response, Result.fail(ErrorCode.ACCESS_TOKEN_EXPIRED));
            return;
        } catch (JwtException | IllegalArgumentException e) {
            ServletUtil.write(response, Result.fail(ErrorCode.ACCESS_TOKEN_INVALID));
            return;
        }
        // 获取用户
        User user = Convert.convert(User.class, claims.get(Constant.USER));
        // 获取授权信息
        List<SimpleGrantedAuthority> authorities = Convert.toList(String.class, claims.get(Constant.AUTHORITIES)).stream().map(SimpleGrantedAuthority::new).toList();
        // 传递用户的认证信息 -> 将用户标识为已认证状态
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
        // authenticationToken放到安全上下文中
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }
}
