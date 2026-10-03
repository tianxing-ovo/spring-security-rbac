package com.ltx.config;

import cn.hutool.core.util.StrUtil;
import com.ltx.constant.Constant;
import com.ltx.entity.Result;
import com.ltx.entity.SecurityUser;
import com.ltx.entity.User;
import com.ltx.enums.ErrorCode;
import com.ltx.filter.JwtFilter;
import com.ltx.util.JwtUtil;
import com.ltx.util.RedisUtil;
import com.ltx.util.ServletUtil;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * 安全配置
 *
 * @author tianxing
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final RedisUtil redisUtil;

    /**
     * 配置过滤器链
     *
     * @param http {@link HttpSecurity} 安全配置对象
     * @return 过滤器链 {@link SecurityFilterChain}
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 在UsernamePasswordAuthenticationFilter前添加Jwt过滤器
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        // 放行刷新接口其余请求均需认证
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/refresh").permitAll().anyRequest().authenticated());
        // 登录成功后签发双Token(AccessToken + RefreshToken)
        http.formLogin(form -> form.successHandler((request, response, authentication) -> {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            User user = securityUser.getUser();
            // 用户授权信息
            List<String> authorities = securityUser.getAuthorities().stream().map(SimpleGrantedAuthority::getAuthority).toList();
            // 使用JWS生成AccessToken
            String accessToken = JwtUtil.createJws(user, authorities, Constant.ACCESS_TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
            // 使用UUID生成RefreshToken
            String refreshToken = UUID.randomUUID().toString().replace("-", "");
            // 将RefreshToken存入Redis
            String key = Constant.REFRESH_TOKEN_KEY + refreshToken;
            // {key = refresh:token:<refreshToken>, value = <username>, timeout = 7 days}
            redisUtil.set(key, user.getUsername(), Constant.REFRESH_TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
            // 返回双Token(accessToken + refreshToken)
            Result result = Result.success("登录成功")
                    .put(Constant.ACCESS_TOKEN, accessToken)
                    .put(Constant.REFRESH_TOKEN, refreshToken);
            ServletUtil.write(response, result);
        }).failureHandler((request, response, exception) -> {
            Result result = Result.fail(ErrorCode.LOGIN_FAILED);
            ServletUtil.write(response, result);
        }).permitAll());
        // 退出成功后删除Redis中的RefreshToken
        http.logout(logout -> logout.logoutSuccessHandler((request, response, authentication) -> {
            // 从请求头中获取RefreshToken
            String refreshToken = request.getHeader(Constant.REFRESH_TOKEN);
            if (StrUtil.isNotBlank(refreshToken)) {
                // 删除Redis中的RefreshToken
                redisUtil.delete(Constant.REFRESH_TOKEN_KEY + refreshToken);
            }
            ServletUtil.write(response, Result.success("退出成功"));
        }));
        // 设置访问拒绝处理器
        http.exceptionHandling(exception -> exception.accessDeniedHandler((request, response, accessDeniedException) -> {
            Result result = Result.fail(ErrorCode.UNAUTHORIZED);
            ServletUtil.write(response, result);
        }));
        // 禁用CSRF
        http.csrf(AbstractHttpConfigurer::disable);
        // 不创建session
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    /**
     * 配置密码编码器(BCrypt算法)
     *
     * @return 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 打印安全过滤器链
     *
     * @param filterChain 过滤器链
     * @return 应用程序运行器
     */
    @Bean
    public ApplicationRunner printSecurityFilterChain(SecurityFilterChain filterChain) {
        return args -> {
            StringBuilder sb = new StringBuilder("\n========== 安全过滤器链 ==========\n");
            int order = 1;
            for (Filter filter : filterChain.getFilters()) {
                sb.append(String.format("[%02d] %s%n", order++, filter.getClass().getSimpleName()));
            }
            sb.append("====================================");
            log.info(sb.toString());
        };
    }

}
