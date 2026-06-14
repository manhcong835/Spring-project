package com.spring.project.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Điều hướng user sau khi đăng nhập thành công.
 * - ADMIN → /admin/dashboard
 * - CUSTOMER/STAFF → / (trang chủ)
 *
 * Dùng chung cho cả formLogin (LOCAL) và oauth2Login (Google).
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String loginSource = (String) request.getSession().getAttribute("loginSource");

        // Nếu vừa reset mật khẩu → chuyển thẳng sang trang đổi mật khẩu mới
        String justReset = (String) request.getSession().getAttribute("justResetPasswordEmail");
        if (justReset != null && justReset.equalsIgnoreCase(authentication.getName())) {
            response.sendRedirect("/profile?forceChange=true");
            return;
        }

        boolean canAccessAdmin = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String a = authority.getAuthority();
            if ("ROLE_ADMIN".equals(a) || "ROLE_STAFF".equals(a)) {
                canAccessAdmin = true;
                break;
            }
        }

        if (canAccessAdmin && "admin".equals(loginSource)) {
            response.sendRedirect("/admin/dashboard");
        } else {
            response.sendRedirect("/");
        }
    }
}
