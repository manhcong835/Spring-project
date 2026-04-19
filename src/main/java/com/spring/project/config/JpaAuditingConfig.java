package com.spring.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Cấu hình bật JPA Auditing để @CreatedDate và @LastModifiedDate
 * trong BaseEntity tự động được gán giá trị khi create/update.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // Không cần thêm gì nếu không dùng AuditorAware (người dùng hiện tại)
    // Nếu sau này cần lưu createdBy / updatedBy, thêm AuditorAware bean ở đây
}
