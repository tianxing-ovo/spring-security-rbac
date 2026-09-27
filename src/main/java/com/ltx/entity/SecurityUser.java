package com.ltx.entity;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.List;

/**
 * 安全用户
 *
 * @author tianxing
 */
@Getter
public class SecurityUser implements UserDetails {

    @Serial
    private static final long serialVersionUID = -2672594260930233276L;
    // 用户
    private final User user;
    // 密码(仅用于登录校验)
    private final String password;
    // 存放用户的授权信息: 包括角色(Role)和权限(Permission)
    private final List<SimpleGrantedAuthority> authorities;

    public SecurityUser(User user, List<SimpleGrantedAuthority> authorities) {
        password = user.getPassword();
        // 擦除密码防止对外泄露
        user.setPassword("");
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getAccountNonExpired() == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked() == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getCredentialsNonExpired() == 1;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled() == 1;
    }
}
