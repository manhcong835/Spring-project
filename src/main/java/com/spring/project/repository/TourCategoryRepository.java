package com.spring.project.repository;

import com.spring.project.entity.TourCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho TourCategory entity.
 */
@Repository
public interface TourCategoryRepository extends JpaRepository<TourCategory, Long> {

    /**
     * Tìm danh mục theo tên
     */
    Optional<TourCategory> findByName(String name);

    /**
     * Lấy danh mục theo trạng thái (ACTIVE)
     */
    List<TourCategory> findByStatus(String status);

    /**
     * Kiểm tra tên danh mục đã tồn tại chưa
     */
    boolean existsByName(String name);
}
