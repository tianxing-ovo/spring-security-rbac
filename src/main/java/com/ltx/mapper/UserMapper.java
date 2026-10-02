package com.ltx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ltx.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户映射接口
 *
 * @author tianxing
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
