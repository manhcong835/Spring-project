package com.spring.project.service;

import com.spring.project.dto.RegisterRequest;

/**
 * Service xử lý nghiệp vụ xác thực: đăng ký, (đăng nhập do Spring Security xử lý).
 */
public interface AuthService {

    /**
     * Đăng ký tài khoản LOCAL mới.
     * @param request thông tin đăng ký từ form
     * @throws IllegalArgumentException nếu email/phone trùng hoặc password không khớp
     */
    void register(RegisterRequest request);
}
