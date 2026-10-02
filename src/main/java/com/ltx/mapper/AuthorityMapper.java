package com.ltx.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限映射接口
 *
 * @author tianxing
 */
@Mapper
public interface AuthorityMapper {

    /**
     * 通过用户id获取权限
     *
     * @param id 用户id
     * @return 权限列表
     */
    List<String> getAuthById(@Param("id") Long id);

    /**
     * 通过用户id获取角色
     *
     * @param id 用户id
     * @return 角色列表
     */
    List<String> getRolesById(@Param("id") Long id);
}