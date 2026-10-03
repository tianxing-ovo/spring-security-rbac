package com.ltx.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 常量类
 *
 * @author tianxing
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constant {

    // 用户
    public static final String USER = "user";
    // 权限列表
    public static final String AUTHORITIES = "authorities";
    // 角色前缀: Spring Security规定好的
    public static final String ROLE_PREFIX = "ROLE_";
    // 访问令牌有效期: 30分钟
    public static final long ACCESS_TOKEN_EXPIRE_MINUTES = 30;
    // 刷新令牌有效期: 7天
    public static final long REFRESH_TOKEN_EXPIRE_DAYS = 7;
    // 刷新令牌在Redis中的键前缀
    public static final String REFRESH_TOKEN_KEY = "refresh:token:";
    // 请求头与响应参数名称
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
}
