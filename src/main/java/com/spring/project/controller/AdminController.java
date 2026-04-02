package com.spring.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // ==================== AUTHENTICATION ====================

    @GetMapping("/login")
    public String login() {
        return "admin/pages/login";
    }

    // ==================== DASHBOARD ====================

    @GetMapping({ "", "/dashboard" })
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
}
