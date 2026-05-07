package com.spring.project.service.impl;

import com.spring.project.dto.PromotionRequest;
import com.spring.project.entity.Promotion;
import com.spring.project.repository.BookingRepository;
import com.spring.project.repository.PromotionRepository;
import com.spring.project.service.PromotionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation quản lý Khuyến mãi — UC Admin 4.
 */
@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final BookingRepository bookingRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository,
                                 BookingRepository bookingRepository) {
        this.promotionRepository = promotionRepository;
        this.bookingRepository = bookingRepository;
    }

    // ==================== UC 4.1 — Xem danh sách ====================

    @Override
    public Page<Promotion> getPromotionList(String status, Pageable pageable) {
        if (status != null && !status.isBlank()) {
            return promotionRepository.findByStatus(status, pageable);
        }
        return promotionRepository.findAll(pageable);
    }

    // ==================== getPromotionById ====================

    @Override
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khuyến mãi không tồn tại (ID: " + id + ")"));
    }

    // ==================== UC 4.2 — Thêm khuyến mãi ====================

    @Override
    @Transactional
    public void createPromotion(PromotionRequest request) {
        String code = request.getCode().trim().toUpperCase();

        // 1. Kiểm tra mã trùng
        if (promotionRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Mã khuyến mãi đã tồn tại: " + code);
        }

        // 2. Validate ngày
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        // 3. Validate PERCENT <= 100
        if ("PERCENT".equals(request.getDiscountType())
                && request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Phần trăm giảm giá không được vượt quá 100%");
        }

        // 4. Tạo entity
        Promotion promotion = new Promotion();
        promotion.setCode(code);
        promotion.setName(request.getName().trim());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMaxDiscountAmount(request.getMaxDiscountAmount());
        promotion.setMinBookingAmount(request.getMinBookingAmount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setUsedCount(0);
        promotion.setStatus("ACTIVE");

        promotionRepository.save(promotion);
    }

    // ==================== UC 4.3 — Cập nhật ====================

    @Override
    @Transactional
    public void updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = getPromotionById(id);
        String code = request.getCode().trim().toUpperCase();

        // 1. Kiểm tra code trùng (loại trừ chính mình)
        if (!promotion.getCode().equals(code) && promotionRepository.existsByCodeAndIdNot(code, id)) {
            throw new IllegalArgumentException("Mã khuyến mãi đã tồn tại: " + code);
        }

        // 2. Validate ngày
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        // 3. Validate PERCENT <= 100
        if ("PERCENT".equals(request.getDiscountType())
                && request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Phần trăm giảm giá không được vượt quá 100%");
        }

        // 4. Validate usageLimit >= usedCount
        if (request.getUsageLimit() != null && promotion.getUsedCount() > request.getUsageLimit()) {
            throw new IllegalArgumentException(
                    "Giới hạn lượt dùng không được nhỏ hơn số đã sử dụng (" + promotion.getUsedCount() + ")");
        }

        // 5. Cập nhật (KHÔNG reset usedCount)
        promotion.setCode(code);
        promotion.setName(request.getName().trim());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMaxDiscountAmount(request.getMaxDiscountAmount());
        promotion.setMinBookingAmount(request.getMinBookingAmount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setUsageLimit(request.getUsageLimit());

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            promotion.setStatus(request.getStatus());
        }

        promotionRepository.save(promotion);
    }

    // ==================== UC 4.4 — Xóa ====================

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        Promotion promotion = getPromotionById(id);

        // Kiểm tra có booking đang dùng promotion này
        boolean hasActiveBookings = bookingRepository
                .existsByPromotion_IdAndBookingStatusIn(id, List.of("PENDING", "CONFIRMED"));

        if (hasActiveBookings) {
            // Soft delete: chuyển sang INACTIVE
            promotion.setStatus("INACTIVE");
            promotionRepository.save(promotion);
        } else {
            // Hard delete
            promotionRepository.delete(promotion);
        }
    }
}
