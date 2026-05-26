package com.spring.project.service;

import com.spring.project.entity.Review;

/**
 * Service quản lý đánh giá — UC 7.
 */
public interface ReviewService {

    /** UC 7 — Tạo đánh giá cho booking đã hoàn thành */
    Review createReview(Long bookingId, Long userId, int rating, String title, String content);

    /** UC 7 — Kiểm tra user có quyền đánh giá booking không */
    boolean canReview(Long bookingId, Long userId);
}
