package com.ltx.entity;


import lombok.Data;


/**
 * 角色
 *
 * @author tianxing
 */
@Data
@SuppressWarnings("unused")
public class Role {
    private Long id;
    // 角色名称
    private String roleName;
    // 角色描述
    private String roleDescription;
}
