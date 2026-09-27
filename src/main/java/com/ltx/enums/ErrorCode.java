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
    UNAUTHORIZED(201, "权限不足"),
    LOGIN_FAILED(202, "登录失败"),
    ACCESS_TOKEN_IS_NULL(203, "accessToken为空"),
    ACCESS_TOKEN_EXPIRED(204, "accessToken过期"),
    ACCESS_TOKEN_INVALID(205, "accessToken无效"),
    USER_HAS_EXITED(206, "用户已退出"),
    REFRESH_TOKEN_IS_NULL(207, "refreshToken为空"),
    REFRESH_TOKEN_EXPIRED(208, "refreshToken过期");

    private final int code;
    private final String message;
}
