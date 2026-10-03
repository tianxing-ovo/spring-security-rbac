package com.ltx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误状态码枚举
 *
 * @author tianxing
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
    LOGIN_FAILED(40001, "登录失败"),
    ACCESS_TOKEN_IS_NULL(40101, "accessToken为空"),
    ACCESS_TOKEN_EXPIRED(40102, "accessToken过期"),
    ACCESS_TOKEN_INVALID(40103, "accessToken无效"),
    REFRESH_TOKEN_IS_NULL(40104, "refreshToken为空"),
    REFRESH_TOKEN_EXPIRED(40105, "refreshToken过期"),
    UNAUTHORIZED(40301, "权限不足");

    private final int code;
    private final String message;
}
