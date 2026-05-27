package com.spring.project.service.impl;

import com.spring.project.dto.RegisterRequest;
import com.spring.project.entity.Role;
import com.spring.project.entity.User;
import com.spring.project.entity.UserAuthProvider;
import com.spring.project.repository.RoleRepository;
import com.spring.project.repository.UserAuthProviderRepository;
import com.spring.project.repository.UserRepository;
import com.spring.project.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation đăng ký LOCAL.
 * Luồng: validate → tạo User → tạo UserAuthProvider(LOCAL) với password đã hash.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           UserAuthProviderRepository userAuthProviderRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {

        // 1. Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email này đã được sử dụng");
        }

        // 2. Kiểm tra phone (nếu có nhập)
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new IllegalArgumentException("Số điện thoại này đã được sử dụng");
            }
        }

        // 3. Kiểm tra confirmPassword khớp
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }

        // 4. Tìm Role CUSTOMER (phải tồn tại sẵn trong DB)
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại trong database"));

        // 5. Tạo User entity (password để null — đã chuyển sang UserAuthProvider)
        User user = new User();
        user.setRole(customerRole);
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus("ACTIVE");
        User savedUser = userRepository.save(user);

        // 6. Tạo UserAuthProvider cho LOCAL login
        UserAuthProvider authProvider = new UserAuthProvider();
        authProvider.setUser(savedUser);
        authProvider.setProvider("LOCAL");
        authProvider.setPassword(passwordEncoder.encode(request.getPassword()));
        authProvider.setEmailVerified(true);
        authProvider.setProviderEmail(request.getEmail());
        userAuthProviderRepository.save(authProvider);
    }
}
