package com.spring.project.repository;

import com.spring.project.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Payment entity.
 * Use Case: Thanh toán, xem lịch sử thanh toán.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Tìm giao dịch theo mã thanh toán
     */
    Optional<Payment> findByPaymentCode(String paymentCode);

    /**
     * Tìm giao dịch theo transaction ref (từ cổng thanh toán VNPAY/MOMO)
     */
    Optional<Payment> findByTransactionRef(String transactionRef);

    /**
     * Lấy tất cả giao dịch của một booking
     */
    List<Payment> findByBookingId(Long bookingId);

    /**
     * Lấy giao dịch thành công của một booking
     */
    List<Payment> findByBookingIdAndStatus(Long bookingId, String status);
}
