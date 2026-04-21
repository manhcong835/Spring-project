package com.spring.project.security;

import com.spring.project.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Wrapper chứa thông tin User (từ DB) + passwordHash.
 * Spring Security dùng object này làm "principal" sau khi login LOCAL thành công.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final String passwordHash;

    public CustomUserDetails(User user, String passwordHash) {
        this.user = user;
        this.passwordHash = passwordHash;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equals(user.getStatus());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"BANNED".equals(user.getStatus());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Helper method để lấy User entity từ principal.
     * Dùng trong Controller/Service khi cần lấy thông tin user đang đăng nhập.
     */
    public User getUser() {
        return user;
    }

    public Long getUserId() {
        return user.getId();
    }
}
