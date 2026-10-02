package com.ltx.constant;

/**
 * 常量接口
 *
 * @author tianxing
 */
public interface Constant {
    // 用户
    String USER = "user";
    // 权限列表
    String AUTHORITIES = "authorities";
    // 角色前缀: Spring Security规定好的
    String ROLE_PREFIX = "ROLE_";
    // 访问令牌有效期: 30分钟
    long ACCESS_TOKEN_EXPIRE_MINUTES = 30;
    // 刷新令牌有效期: 7天
    long REFRESH_TOKEN_EXPIRE_DAYS = 7;
    // 刷新令牌在Redis中的键前缀
    String REFRESH_TOKEN_KEY = "refresh:token:";
    // 请求头与响应参数名称
    String ACCESS_TOKEN = "accessToken";
    String REFRESH_TOKEN = "refreshToken";
}
