package com.spring.project.repository;

import com.spring.project.entity.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Tour entity.
 * Use Case: Tìm kiếm tour, lọc tour, xem danh sách tour (Customer & Admin).
 */
@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    /**
     * Tìm tour theo slug (dùng cho URL SEO-friendly)
     */
    Optional<Tour> findBySlug(String slug);

    /**
     * Tìm tour theo mã tour
     */
    Optional<Tour> findByCode(String code);

    /**
     * UC 2.2 - Lọc tour theo danh mục
     */
    Page<Tour> findByCategoryIdAndStatus(Long categoryId, String status, Pageable pageable);

    /**
     * UC 2.2 - Lọc tour theo điểm đến
     */
    Page<Tour> findByDestinationIdAndStatus(Long destinationId, String status, Pageable pageable);

    /**
     * UC 2.1 / 2.2 - Tìm kiếm + lọc tour nâng cao theo tên hoặc điểm đến
     * Phục vụ cả Customer search và Admin quản lý tour
     */
    @Query("SELECT t FROM Tour t WHERE t.status = 'ACTIVE' AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.destination.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.departureLocation) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Tour> searchToursByNameOrDestination(@Param("keyword") String keyword, Pageable pageable);

    /**
     * UC 3 - Tìm kiếm tour còn chỗ (có chuyến khởi hành available)
     * UC 2.2 - Lọc tour còn slot trống
     */
    @Query("SELECT DISTINCT t FROM Tour t " +
           "JOIN t.departures d " +
           "WHERE t.status = 'ACTIVE' AND d.status = 'OPEN' AND d.availableSlots > 0")
    List<Tour> findAvailableTours();

    /**
     * Tìm kiếm tour phân trang dành cho Admin (không lọc status)
     */
    @Query("SELECT t FROM Tour t WHERE " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Tour> searchToursForAdmin(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Lấy tour theo trạng thái (phân trang cho Admin)
     */
    Page<Tour> findByStatus(String status, Pageable pageable);
}
