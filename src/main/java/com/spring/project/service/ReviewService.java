package com.spring.project.service;

import com.spring.project.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service quản lý đánh giá — UC 7 + Admin quản lý đánh giá.
 */
public interface ReviewService {

    /** UC 7 — Tạo đánh giá cho booking đã hoàn thành */
    Review createReview(Long bookingId, Long userId, int rating, String title, String content);

    /** UC 7 — Kiểm tra user có quyền đánh giá booking không */
    boolean canReview(Long bookingId, Long userId);

    /** Admin — Danh sách review có phân trang, lọc theo status (null = tất cả). */
    Page<Review> getReviewList(String status, Pageable pageable);

    /** Admin — Lấy review theo id (throw nếu không tồn tại). */
    Review getReviewById(Long id);

    /** Admin — Ẩn/hiện review: toggle status VISIBLE <-> HIDDEN. */
    void toggleStatus(Long id);

    /** Admin — Xóa review. */
    void deleteReview(Long id);
}
