package com.spring.project.config;

import com.spring.project.security.CustomAuthenticationSuccessHandler;
import com.spring.project.security.CustomOAuth2UserService;
import com.spring.project.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Cấu hình Spring Security:
 * - Phân quyền URL (public / authenticated / admin)
 * - Form login LOCAL (email + password)
 * - OAuth2 login Google
 * - SuccessHandler: phân biệt redirect ADMIN vs CUSTOMER
 * - Logout
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final ObjectProvider<DevAutoLoginFilter> devAutoLoginFilterProvider;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          CustomUserDetailsService customUserDetailsService,
                          CustomAuthenticationSuccessHandler successHandler,
                          ObjectProvider<DevAutoLoginFilter> devAutoLoginFilterProvider) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customUserDetailsService = customUserDetailsService;
        this.successHandler = successHandler;
        this.devAutoLoginFilterProvider = devAutoLoginFilterProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Cho phép truy cập công khai
                .requestMatchers(
                    "/", "/home", "/index",
                    "/login", "/register", "/register/verify", "/register/resend-otp",
                    "/admin", "/admin/", "/admin/login",
                    "/forgot-password",
                    "/tours", "/tours/**",
                    "/about", "/contact",
                    "/css/**", "/js/**", "/images/**", "/fonts/**", "/assets/**", "/uploads/**",
                    "/error/**"
                ).permitAll()

                // Chỉ ADMIN: tài chính, nhân sự, marketing, thao tác khách hàng
                .requestMatchers(
                    "/admin/revenue/**",
                    "/admin/staff/**",
                    "/admin/promotions/**",
                    "/admin/customers/create",
                    "/admin/customers/status"
                ).hasRole("ADMIN")

                // ADMIN + STAFF: dashboard, tour, danh mục, điểm đến, đơn đặt, đánh giá, hồ sơ
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "STAFF")

                // Các URL còn lại yêu cầu đăng nhập
                .anyRequest().authenticated()
            )

            // ===== Form Login (đăng nhập LOCAL) =====
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // ===== OAuth2 Login (đăng nhập Google) =====
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
            )

            // ===== Logout =====
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )

            // ===== UserDetailsService =====
            .userDetailsService(customUserDetailsService);

        // ===== [DEV] Auto-login Admin =====
        devAutoLoginFilterProvider.ifAvailable(filter ->
            http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
        );

        return http.build();
    }
}
