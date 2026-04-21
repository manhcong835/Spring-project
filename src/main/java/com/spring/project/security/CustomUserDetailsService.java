package com.spring.project.security;

import com.spring.project.entity.User;
import com.spring.project.entity.UserAuthProvider;
import com.spring.project.repository.UserAuthProviderRepository;
import com.spring.project.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Load user từ DB khi đăng nhập bằng email/password (LOCAL login).
 * Spring Security gọi loadUserByUsername() khi xử lý POST /login.
 *
 * Luồng:
 * 1. Tìm User theo email
 * 2. Kiểm tra status != BANNED
 * 3. Tìm UserAuthProvider(userId, "LOCAL")
 * 4. Trả CustomUserDetails(user, passwordHash)
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    UserAuthProviderRepository userAuthProviderRepository) {
        this.userRepository = userRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Tìm User theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email không tồn tại"));

        // 2. Kiểm tra tài khoản có bị khóa không
        if ("BANNED".equals(user.getStatus())) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa");
        }

        // 3. Tìm UserAuthProvider với provider = LOCAL
        UserAuthProvider authProvider = userAuthProviderRepository
                .findByUserIdAndProvider(user.getId(), "LOCAL")
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản chưa đặt mật khẩu. Vui lòng đăng nhập bằng Google."));

        // 4. Trả về CustomUserDetails với password hash
        return new CustomUserDetails(user, authProvider.getPassword());
    }
}
