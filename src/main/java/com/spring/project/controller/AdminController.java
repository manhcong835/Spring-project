package com.spring.project.controller;

import com.spring.project.dto.ChangePasswordRequest;
import com.spring.project.dto.RegisterRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.User;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.AuthService;
import com.spring.project.service.StaffService;
import com.spring.project.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AuthService authService;
    private final StaffService staffService;

    public AdminController(UserService userService, AuthService authService, StaffService staffService) {
        this.userService = userService;
        this.authService = authService;
        this.staffService = staffService;
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
    public String staffList(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String status,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<User> staffPage = staffService.getStaffList(keyword, status, pageable);
        model.addAttribute("staffPage", staffPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("currentUserId", SecurityUtils.getCurrentUserId());
        return "admin/pages/stafflist";
    }

    @GetMapping("/staff/create")
    public String staffCreate(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "admin/pages/staffcreate";
    }

    @PostMapping("/staff/create")
    public String staffCreatePost(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerRequest", bindingResult);
            redirectAttributes.addFlashAttribute("registerRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/staff/create";
        }
        try {
            staffService.createStaff(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nhân viên thành công!");
            return "redirect:/admin/staff";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("registerRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/staff/create";
        }
    }

    @GetMapping("/staff/update")
    public String staffUpdate(@RequestParam Long id, Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            User staff = staffService.getStaffById(id);
            model.addAttribute("staff", staff);
            if (!model.containsAttribute("updateProfileRequest")) {
                UpdateProfileRequest profileReq = new UpdateProfileRequest();
                profileReq.setFullName(staff.getFullName());
                profileReq.setPhone(staff.getPhone());
                profileReq.setGender(staff.getGender());
                profileReq.setDateOfBirth(staff.getDateOfBirth() != null ? staff.getDateOfBirth().toString() : "");
                profileReq.setAddress(staff.getAddress());
                model.addAttribute("updateProfileRequest", profileReq);
            }
            return "admin/pages/staffupdate";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nhân viên không tồn tại.");
            return "redirect:/admin/staff";
        }
    }

    @PostMapping("/staff/update")
    public String staffUpdatePost(@RequestParam Long id,
                                  @Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest request,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateProfileRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/staff/update?id=" + id;
        }
        try {
            staffService.updateStaff(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhân viên thành công!");
            return "redirect:/admin/staff";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/staff/update?id=" + id;
        }
    }

    @GetMapping("/staff/delete")
    public String staffDelete(@RequestParam Long id, Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            User staff = staffService.getStaffById(id);
            model.addAttribute("staff", staff);
            model.addAttribute("currentUserId", SecurityUtils.getCurrentUserId());
            return "admin/pages/staffdelete";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nhân viên không tồn tại.");
            return "redirect:/admin/staff";
        }
    }

    @PostMapping("/staff/delete")
    public String staffDeletePost(@RequestParam Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            staffService.deleteStaff(id);
            redirectAttributes.addFlashAttribute("successMessage", "Nhân viên đã chuyển trạng thái nghỉ việc!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nhân viên không tồn tại.");
        }
        return "redirect:/admin/staff";
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

    @GetMapping("/tours/departures")
    public String tourDepartures() {
        return "admin/pages/tourdepartures";
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

    @GetMapping("/customers/create")
    public String customerCreate(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "admin/pages/customercreate";
    }

    @PostMapping("/customers/create")
    public String customerCreatePost(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerRequest", bindingResult);
            redirectAttributes.addFlashAttribute("registerRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/customers/create";
        }

        try {
            authService.register(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm khách hàng thành công!");
            return "redirect:/admin/customers/create";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("registerRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/customers/create";
        }
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
