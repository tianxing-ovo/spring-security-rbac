package com.ltx.entity;


import lombok.Data;


/**
 * 权限
 *
 * @author tianxing
 */
@Data
@SuppressWarnings("unused")
public class Authority {
    private Long id;
    // 权限名称
    private String authorityName;
    // 权限描述
    private String authorityDescription;
}
