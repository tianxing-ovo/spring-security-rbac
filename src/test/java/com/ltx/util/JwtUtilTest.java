package com.ltx.util;

import org.junit.jupiter.api.Test;

/**
 * JWT工具类测试
 *
 * @author tianxing
 */
class JwtUtilTest {

    @Test
    void genSecret() {
        System.out.println(JwtUtil.genSecret());
    }
}