package com.spring.project.controller;

import com.spring.project.dto.ChangePasswordRequest;
import com.spring.project.dto.RegisterRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.User;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.AuthService;
import com.spring.project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    // ==================== TRANG CHỦ ====================
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping({ "/", "/home" })
    public String home() {
        return "client/pages/home";
    }

    // @GetMapping("/index")
    // public String index() {
    // return "client/pages/home";
    // }

    // ==================== AUTHENTICATION ====================

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String registered,
            HttpSession session,
            Model model) {

        // Đánh dấu nguồn đăng nhập để SuccessHandler biết
        session.setAttribute("loginSource", "client");

        if (error != null)
            model.addAttribute("errorMessage", "Email hoặc mật khẩu không đúng");
        if (logout != null)
            model.addAttribute("logoutMessage", "Đã đăng xuất thành công");
        if (registered != null)
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "client/pages/login";
    }

    // POST /login do Spring Security tự xử lý — KHÔNG viết ở đây

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "client/pages/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Nếu có lỗi validation (annotation trên DTO)
        if (bindingResult.hasErrors()) {
            return "client/pages/register";
        }

        try {
            authService.register(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // Lỗi nghiệp vụ (email trùng, phone trùng, password không khớp)
            model.addAttribute("errorMessage", e.getMessage());
            return "client/pages/register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "client/pages/forgotpassword";
    }

    // ==================== PROFILE ====================

    @GetMapping("/profile")
    public String profile(@RequestParam(required = false) String success,
                          @RequestParam(required = false) String passwordChanged,
                          Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userService.getUserById(userId);
        boolean hasLocalProvider = userService.hasLocalProvider(userId);

        model.addAttribute("user", user);
        model.addAttribute("hasLocalProvider", hasLocalProvider);

        // Thêm DTO rỗng nếu chưa có trong model (lần đầu vào trang)
        if (!model.containsAttribute("updateProfileRequest")) {
            UpdateProfileRequest profileReq = new UpdateProfileRequest();
            profileReq.setFullName(user.getFullName());
            profileReq.setPhone(user.getPhone());
            profileReq.setGender(user.getGender());
            profileReq.setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "");
            profileReq.setAddress(user.getAddress());
            model.addAttribute("updateProfileRequest", profileReq);
        }
        if (!model.containsAttribute("changePasswordRequest")) {
            model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        }

        // Thông báo thành công
        if (success != null) model.addAttribute("successMessage", "Cập nhật thông tin thành công!");
        if (passwordChanged != null) model.addAttribute("successMessage", "Đổi mật khẩu thành công!");

        return "client/pages/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateProfileRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("profileError", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/profile";
        }

        try {
            Long userId = SecurityUtils.getCurrentUserId();
            User updatedUser = userService.updateProfile(userId, request);

            // Đồng bộ lại principal trong SecurityContextHolder
            SecurityUtils.refreshAuthentication(updatedUser);

            return "redirect:/profile?success=true";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("profileError", e.getMessage());
            return "redirect:/profile";
        }
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordRequest", bindingResult);
            redirectAttributes.addFlashAttribute("changePasswordRequest", request);
            redirectAttributes.addFlashAttribute("passwordError", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/profile";
        }

        try {
            Long userId = SecurityUtils.getCurrentUserId();
            userService.changePassword(userId, request);
            return "redirect:/profile?passwordChanged=true";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("changePasswordRequest", request);
            redirectAttributes.addFlashAttribute("passwordError", e.getMessage());
            return "redirect:/profile";
        }
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
