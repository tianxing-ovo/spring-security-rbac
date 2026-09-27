package com.ltx.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ltx.constant.Constant;
import com.ltx.entity.SecurityUser;
import com.ltx.entity.User;
import com.ltx.mapper.AuthorityMapper;
import com.ltx.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户详情服务实现类
 *
 * @author tianxing
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final AuthorityMapper authorityMapper;

    /**
     * 根据用户名加载用户详情
     *
     * @param username 用户名
     * @return 用户详情
     * @throws UsernameNotFoundException 用户名未找到异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        // 根据用户名查询用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户名未找到");
        }
        Long id = user.getId();
        // 根据用户id查询用户所有角色和权限
        List<String> roleList = authorityMapper.getRolesById(id);
        List<String> authorityList = authorityMapper.getAuthById(id);
        // 存放用户的授权信息: 包括角色(Role)和权限(Permission)
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 将角色加入授权信息
        roleList.forEach(role -> authorities.add(new SimpleGrantedAuthority(Constant.ROLE_PREFIX + role)));
        // 将权限加入授权信息
        authorityList.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority)));
        return new SecurityUser(user, authorities);
    }
}
