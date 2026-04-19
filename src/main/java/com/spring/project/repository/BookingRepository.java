package com.spring.project.repository;

import com.spring.project.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Booking entity.
 * Use Case: Đặt tour, sửa/hủy booking, xem lịch sử, Admin quản lý đơn.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Tìm booking theo mã (dùng trong trang chi tiết booking)
     */
    Optional<Booking> findByBookingCode(String bookingCode);

    /**
     * UC 6 - Xem lịch sử đặt tour: Lấy tất cả booking của một khách hàng
     */
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * UC 6 - Xem lịch sử đặt tour (phân trang)
     */
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    /**
     * Admin 3.1 - Xem danh sách đơn đặt theo trạng thái
     */
    Page<Booking> findByBookingStatus(String bookingStatus, Pageable pageable);

    /**
     * Lấy booking theo user và trạng thái
     */
    List<Booking> findByUserIdAndBookingStatus(Long userId, String bookingStatus);

    /**
     * Tìm booking theo khách hàng và tour cụ thể (để kiểm tra trước khi đánh giá)
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId " +
           "AND b.tourDeparture.tour.id = :tourId " +
           "AND b.bookingStatus = 'COMPLETED'")
    List<Booking> findCompletedBookingsByUserAndTour(
            @Param("userId") Long userId,
            @Param("tourId") Long tourId
    );

    /**
     * Admin 3.1 - Tìm kiếm đơn đặt theo mã hoặc tên khách
     */
    @Query("SELECT b FROM Booking b WHERE " +
           "LOWER(b.bookingCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.contactName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.contactEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Booking> searchBookings(@Param("keyword") String keyword, Pageable pageable);

    /**
     * findBookingsByCustomerId - Lấy booking theo ID khách hàng
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :customerId ORDER BY b.createdAt DESC")
    List<Booking> findBookingsByCustomerId(@Param("customerId") Long customerId);
}
