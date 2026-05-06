package com.spring.project.config;

import com.spring.project.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * [DEV ONLY] Tự động đăng nhập bằng tài khoản Admin khi khởi chạy.
 *
 * Khi bật cấu hình này, mọi request sẽ tự động được xác thực
 * bằng tài khoản admin@tourbooking.com nếu chưa có ai đăng nhập.
 *
 * ⚠️ KHÔNG SỬ DỤNG TRONG PRODUCTION!
 * Để tắt: Xóa hoặc comment @Component trong class này.
 */
@Component
public class DevAutoLoginFilter extends OncePerRequestFilter {

    private static final String ADMIN_EMAIL = "admin@tourbooking.com";

    private final CustomUserDetailsService userDetailsService;

    public DevAutoLoginFilter(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Bỏ qua static resources
        String path = request.getRequestURI();
        if (path.startsWith("/css/") || path.startsWith("/js/") ||
            path.startsWith("/images/") || path.startsWith("/fonts/") ||
            path.startsWith("/assets/") || path.endsWith(".ico")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Chỉ auto-login nếu chưa có authentication
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
            !SecurityContextHolder.getContext().getAuthentication().isAuthenticated() ||
            "anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {

            try {
                UserDetails adminDetails = userDetailsService.loadUserByUsername(ADMIN_EMAIL);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                adminDetails,
                                null,
                                adminDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                // Admin chưa được tạo trong DB, bỏ qua
            }
        }

        filterChain.doFilter(request, response);
    }
}
