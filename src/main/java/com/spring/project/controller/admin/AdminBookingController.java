package com.spring.project.controller.admin;

import com.spring.project.entity.Booking;
import com.spring.project.entity.Review;
import com.spring.project.service.BookingService;
import com.spring.project.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Quản lý đơn đặt tour và đánh giá cho khu vực Admin.
 */
@Controller
@RequestMapping("/admin")
public class AdminBookingController {

    private final BookingService bookingService;
    private final ReviewService reviewService;

    public AdminBookingController(BookingService bookingService,
                                  ReviewService reviewService) {
        this.bookingService = bookingService;
        this.reviewService = reviewService;
    }

    // ==================== BOOKING MANAGEMENT ====================

    @GetMapping("/bookings")
    public String bookingList(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String status,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Booking> bookingPage = bookingService.getBookingList(keyword, status, pageable);
        model.addAttribute("bookingPage", bookingPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "admin/pages/bookinglist";
    }

    @GetMapping("/bookings/status")
    public String bookingStatusUpdate(@RequestParam Long id, Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.getBookingById(id);
            model.addAttribute("booking", booking);
            model.addAttribute("allowedStatuses", bookingService.getAllowedTransitions(booking.getBookingStatus()));
            return "admin/pages/bookingstatusupdate";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
            return "redirect:/admin/bookings";
        }
    }

    @PostMapping("/bookings/status")
    public String bookingStatusUpdatePost(@RequestParam Long id,
                                           @RequestParam String newStatus,
                                           RedirectAttributes redirectAttributes) {
        try {
            bookingService.updateBookingStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
        }
        return "redirect:/admin/bookings";
    }

    @GetMapping("/bookings/delete")
    public String bookingDelete(@RequestParam Long id, Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.getBookingById(id);
            model.addAttribute("booking", booking);
            return "admin/pages/bookingdelete";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
            return "redirect:/admin/bookings";
        }
    }

    @PostMapping("/bookings/delete")
    public String bookingDeletePost(@RequestParam Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            bookingService.deleteBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa đơn đặt thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
        }
        return "redirect:/admin/bookings";
    }

    // ==================== REVIEW MANAGEMENT ====================

    @GetMapping("/reviews")
    public String reviewList(@RequestParam(required = false) String status,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewService.getReviewList(status, pageable);
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("status", status);
        return "admin/pages/reviewlist";
    }

    @PostMapping("/reviews/toggle")
    public String reviewToggle(@RequestParam Long id,
                               @RequestParam(required = false) String status,
                               @RequestParam(defaultValue = "0") int page,
                               RedirectAttributes redirectAttributes) {
        try {
            reviewService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái đánh giá.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/reviews?page=" + page + (status != null ? "&status=" + status : "");
    }

    @GetMapping("/reviews/delete")
    public String reviewDelete(@RequestParam Long id, Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("review", reviewService.getReviewById(id));
            return "admin/pages/reviewdelete";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/reviews";
        }
    }

    @PostMapping("/reviews/delete")
    public String reviewDeletePost(@RequestParam Long id,
                                    RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReview(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa đánh giá thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/reviews";
    }
}
