package com.spring.project.service;

import com.spring.project.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service quản lý Đơn đặt tour — UC Admin 3.
 */
public interface BookingService {

    /** UC 3.1 — Xem danh sách đơn đặt với tìm kiếm và filter */
    Page<Booking> getBookingList(String keyword, String status, Pageable pageable);

    /** Lấy booking theo ID */
    Booking getBookingById(Long id);

    /** UC 3.2 — Lấy danh sách trạng thái có thể chuyển */
    List<String> getAllowedTransitions(String currentStatus);

    /** UC 3.2 — Cập nhật trạng thái đơn (với pessimistic lock) */
    void updateBookingStatus(Long id, String newStatus);

    /** UC 3.3 — Xóa đơn đặt (soft delete, chỉ đơn CANCELLED) */
    void deleteBooking(Long id);
}
