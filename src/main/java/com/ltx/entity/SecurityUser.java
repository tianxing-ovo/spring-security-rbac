package com.ltx.entity;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class SecurityUser implements UserDetails {

    @Serial
    private static final long serialVersionUID = -2672594260930233276L;
    // 用户
    private final User user;
    // 存放用户的授权信息: 包括角色(Role)和权限(Permission)
    private final List<SimpleGrantedAuthority> authorities;

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
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
