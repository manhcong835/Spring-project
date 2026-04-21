package com.spring.project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // ==================== AUTHENTICATION ====================

    @GetMapping({ "", "/" })
    public String adminRoot(Authentication authentication) {
        if (isAdmin(authentication)) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/login";
    }

    @GetMapping("/login")
    public String login(Authentication authentication,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            HttpSession session,
            Model model) {

        // Đã đăng nhập với role ADMIN thì không cần vào lại trang login
        if (isAdmin(authentication)) {
            return "redirect:/admin/dashboard";
        }

        // Đánh dấu nguồn đăng nhập để SuccessHandler biết
        session.setAttribute("loginSource", "admin");

        if (error != null)
            model.addAttribute("errorMessage", "Email hoặc mật khẩu không đúng");
        if (logout != null)
            model.addAttribute("logoutMessage", "Đã đăng xuất thành công");
        return "admin/pages/login";
    }

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/pages/dashboard";
    }

    // ==================== STAFF MANAGEMENT ====================

    @GetMapping("/staff")
    public String staffList() {
        return "admin/pages/stafflist";
    }

    @GetMapping("/staff/create")
    public String staffCreate() {
        return "admin/pages/staffcreate";
    }

    @GetMapping("/staff/update")
    public String staffUpdate() {
        return "admin/pages/staffupdate";
    }

    @GetMapping("/staff/delete")
    public String staffDelete() {
        return "admin/pages/staffdelete";
    }

    // ==================== TOUR MANAGEMENT ====================

    @GetMapping("/tours")
    public String tourList() {
        return "admin/pages/tourlist";
    }

    @GetMapping("/tours/create")
    public String tourCreate() {
        return "admin/pages/tourcreate";
    }

    @GetMapping("/tours/update")
    public String tourUpdate() {
        return "admin/pages/tourupdate";
    }

    @GetMapping("/tours/delete")
    public String tourDelete() {
        return "admin/pages/tourdelete";
    }

    // ==================== BOOKING MANAGEMENT ====================

    @GetMapping("/bookings")
    public String bookingList() {
        return "admin/pages/bookinglist";
    }

    @GetMapping("/bookings/status")
    public String bookingStatusUpdate() {
        return "admin/pages/bookingstatusupdate";
    }

    @GetMapping("/bookings/delete")
    public String bookingDelete() {
        return "admin/pages/bookingdelete";
    }

    // ==================== PROMOTION MANAGEMENT ====================

    @GetMapping("/promotions")
    public String promotionList() {
        return "admin/pages/promotionlist";
    }

    @GetMapping("/promotions/create")
    public String promotionCreate() {
        return "admin/pages/promotioncreate";
    }

    @GetMapping("/promotions/update")
    public String promotionUpdate() {
        return "admin/pages/promotionupdate";
    }

    @GetMapping("/promotions/delete")
    public String promotionDelete() {
        return "admin/pages/promotiondelete";
    }

    // ==================== CUSTOMER MANAGEMENT ====================

    @GetMapping("/customers")
    public String customerList() {
        return "admin/pages/customerlist";
    }

    @GetMapping("/customers/search")
    public String customerSearch() {
        return "admin/pages/customersearch";
    }

    @GetMapping("/customers/status")
    public String customerStatus() {
        return "admin/pages/customerstatus";
    }

    // ==================== ADMIN PROFILE ====================

    @GetMapping("/profile")
    public String profile() {
        return "admin/pages/profile";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "admin/pages/changepassword";
    }

    // ==================== ERROR PAGES ====================

    @GetMapping("/error/404")
    public String error404() {
        return "admin/pages/error404";
    }

    @GetMapping("/error/500")
    public String error500() {
        return "admin/pages/error500";
    }

    // ==================== HELPER ====================

    /**
     * Kiểm tra user đã đăng nhập và có role ADMIN hay không.
     */
    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
