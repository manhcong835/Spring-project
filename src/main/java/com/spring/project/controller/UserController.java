package com.spring.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class UserController {

    // ==================== TRANG CHỦ ====================

    @GetMapping({ "/", "/home" })
    public String home() {
        return "client/pages/home";
    }

    @GetMapping("/index")
    public String index() {
        return "client/pages/home";
    }

    // ==================== AUTHENTICATION ====================

    @GetMapping("/login")
    public String login() {
        return "client/pages/login";
    }

    @GetMapping("/register")
    public String register() {
        return "client/pages/register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "client/pages/forgotpassword";
    }

    // ==================== PROFILE ====================

    @GetMapping("/profile")
    public String profile() {
        return "client/pages/profile";
    }

    // ==================== TOUR ====================

    @GetMapping("/tours")
    public String tourList() {
        return "client/pages/tourlist";
    }

    @GetMapping("/tours/detail")
    public String tourDetail() {
        return "client/pages/tourdetail";
    }

    // ==================== BOOKING ====================

    @GetMapping("/booking/create")
    public String bookingCreate() {
        return "client/pages/bookingcreate";
    }

    @GetMapping("/booking/edit")
    public String bookingEdit() {
        return "client/pages/bookingedit";
    }

    @GetMapping("/booking/cancel")
    public String bookingCancel() {
        return "client/pages/bookingcancel";
    }

    @GetMapping("/booking/detail")
    public String bookingDetail() {
        return "client/pages/bookingdetail";
    }

    @GetMapping("/booking/history")
    public String bookingHistory() {
        return "client/pages/bookinghistory";
    }

    // ==================== PAYMENT ====================

    @GetMapping("/payment")
    public String payment() {
        return "client/pages/payment";
    }

    // ==================== REVIEW ====================

    @GetMapping("/review/create")
    public String reviewCreate() {
        return "client/pages/reviewcreate";
    }

    // ==================== STATIC PAGES ====================

    @GetMapping("/about")
    public String about() {
        return "client/pages/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "client/pages/contact";
    }

    // ==================== ERROR PAGES ====================

    @GetMapping("/error/404")
    public String error404() {
        return "client/pages/error404";
    }

    @GetMapping("/error/500")
    public String error500() {
        return "client/pages/error500";
    }
}
