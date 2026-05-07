package com.spring.project.service.impl;

import com.spring.project.entity.Booking;
import com.spring.project.entity.TourDeparture;
import com.spring.project.repository.BookingRepository;
import com.spring.project.repository.TourDepartureRepository;
import com.spring.project.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementation quản lý Đơn đặt tour — UC Admin 3.
 */
@Service
public class BookingServiceImpl implements BookingService {

    /**
     * Bảng chuyển trạng thái cho phép.
     * COMPLETED và CANCELLED là final state → không có trong map.
     */
    private static final Map<String, List<String>> ALLOWED_TRANSITIONS = Map.of(
            "PENDING",   List.of("CONFIRMED", "CANCELLED"),
            "CONFIRMED", List.of("COMPLETED", "CANCELLED")
    );

    private final BookingRepository bookingRepository;
    private final TourDepartureRepository tourDepartureRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                               TourDepartureRepository tourDepartureRepository) {
        this.bookingRepository = bookingRepository;
        this.tourDepartureRepository = tourDepartureRepository;
    }

    // ==================== UC 3.1 — Xem danh sách ====================

    @Override
    public Page<Booking> getBookingList(String keyword, String status, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return bookingRepository.searchBookingsWithTour(keyword.trim(), pageable);
        }
        if (status != null && !status.isBlank()) {
            return bookingRepository.findByBookingStatusWithTour(status, pageable);
        }
        return bookingRepository.findAllWithTour(pageable);
    }

    // ==================== getBookingById ====================

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đơn đặt tour không tồn tại (ID: " + id + ")"));
    }

    // ==================== UC 3.2 — Trạng thái ====================

    @Override
    public List<String> getAllowedTransitions(String currentStatus) {
        return ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Collections.emptyList());
    }

    @Override
    @Transactional
    public void updateBookingStatus(Long id, String newStatus) {
        // Pessimistic lock: tránh race condition
        Booking booking = bookingRepository.findByIdWithLock(id)
                .orElseThrow(() -> new RuntimeException("Đơn đặt tour không tồn tại (ID: " + id + ")"));

        String currentStatus = booking.getBookingStatus();

        // Validate transition
        List<String> allowed = getAllowedTransitions(currentStatus);
        if (!allowed.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Không thể chuyển từ " + currentStatus + " sang " + newStatus +
                    ". Trạng thái cho phép: " + allowed);
        }

        // Guard cho COMPLETED: kiểm tra đã thanh toán
        if ("COMPLETED".equals(newStatus)) {
            if (!"PAID".equals(booking.getPaymentStatus())) {
                throw new IllegalArgumentException(
                        "Không thể hoàn thành đơn chưa thanh toán đủ. Trạng thái thanh toán hiện tại: " +
                        booking.getPaymentStatus());
            }
        }

        // Guard cho CANCELLED: trả slot cho departure
        if ("CANCELLED".equals(newStatus) && !"CANCELLED".equals(currentStatus)) {
            TourDeparture departure = booking.getTourDeparture();
            departure.setAvailableSlots(departure.getAvailableSlots() + booking.getTotalPeople());
            // Nếu departure đang FULL → mở lại
            if ("FULL".equals(departure.getStatus())) {
                departure.setStatus("OPEN");
            }
            tourDepartureRepository.save(departure);
        }

        booking.setBookingStatus(newStatus);
        bookingRepository.save(booking);
    }

    // ==================== UC 3.3 — Xóa đơn ====================

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);

        // Chỉ cho xóa đơn đã hủy
        if (!"CANCELLED".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException(
                    "Chỉ có thể xóa đơn đã hủy. Trạng thái hiện tại: " + booking.getBookingStatus());
        }

        // Soft delete
        booking.setBookingStatus("DELETED");
        bookingRepository.save(booking);
    }
}
