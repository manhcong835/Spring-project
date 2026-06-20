package com.spring.project.controller.admin;

import com.spring.project.dto.ChangePasswordRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.User;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.AdminDashboardService;
import com.spring.project.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Quản lý đăng nhập, dashboard, hồ sơ, đổi mật khẩu cho khu vực /admin.
 */
@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private final UserService userService;
    private final AdminDashboardService adminDashboardService;

    public AdminAuthController(UserService userService,
                               AdminDashboardService adminDashboardService) {
        this.userService = userService;
        this.adminDashboardService = adminDashboardService;
    }

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

        if (isAdmin(authentication)) {
            return "redirect:/admin/dashboard";
        }

        session.setAttribute("loginSource", "admin");

        if (error != null)
            model.addAttribute("errorMessage", "Email hoặc mật khẩu không đúng");
        if (logout != null)
            model.addAttribute("logoutMessage", "Đã đăng xuất thành công");
        return "admin/pages/login";
    }

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("dashboard", adminDashboardService.getDashboard());
        return "admin/pages/dashboard";
    }

    // ==================== ADMIN PROFILE ====================

    @GetMapping("/profile")
    public String profile(@RequestParam(required = false) String success,
                          @RequestParam(required = false) String passwordChanged,
                          Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userService.getUserById(userId);

        model.addAttribute("user", user);

        if (!model.containsAttribute("updateProfileRequest")) {
            UpdateProfileRequest profileReq = new UpdateProfileRequest();
            profileReq.setFullName(user.getFullName());
            profileReq.setPhone(user.getPhone());
            profileReq.setGender(user.getGender());
            profileReq.setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "");
            profileReq.setAddress(user.getAddress());
            model.addAttribute("updateProfileRequest", profileReq);
        }

        if (success != null) model.addAttribute("successMessage", "Cập nhật thông tin thành công!");
        if (passwordChanged != null) model.addAttribute("successMessage", "Đổi mật khẩu thành công!");

        return "admin/pages/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateProfileRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("profileError", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/profile";
        }

        try {
            Long userId = SecurityUtils.getCurrentUserId();
            User updatedUser = userService.updateProfile(userId, request);
            SecurityUtils.refreshAuthentication(updatedUser);
            return "redirect:/admin/profile?success=true";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("profileError", e.getMessage());
            return "redirect:/admin/profile";
        }
    }

    @GetMapping("/change-password")
    public String changePassword(Model model) {
        if (!model.containsAttribute("changePasswordRequest")) {
            model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        }
        return "admin/pages/changepassword";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordRequest", bindingResult);
            redirectAttributes.addFlashAttribute("changePasswordRequest", request);
            redirectAttributes.addFlashAttribute("passwordError", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/change-password";
        }

        try {
            Long userId = SecurityUtils.getCurrentUserId();
            userService.changePassword(userId, request);
            return "redirect:/admin/profile?passwordChanged=true";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("changePasswordRequest", request);
            redirectAttributes.addFlashAttribute("passwordError", e.getMessage());
            return "redirect:/admin/change-password";
        }
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

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> "ROLE_ADMIN".equals(a) || "ROLE_STAFF".equals(a));
    }
}
