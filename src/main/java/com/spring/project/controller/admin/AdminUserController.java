package com.spring.project.controller.admin;

import com.spring.project.dto.RegisterRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.User;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.AuthService;
import com.spring.project.service.CustomerService;
import com.spring.project.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
 * Quản lý Nhân viên và Khách hàng cho khu vực Admin.
 */
@Controller
@RequestMapping("/admin")
public class AdminUserController {

    private final StaffService staffService;
    private final CustomerService customerService;
    private final AuthService authService;

    public AdminUserController(StaffService staffService,
                               CustomerService customerService,
                               AuthService authService) {
        this.staffService = staffService;
        this.customerService = customerService;
        this.authService = authService;
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

    // ==================== CUSTOMER MANAGEMENT ====================

    @GetMapping("/customers")
    public String customerList(@RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<User> customerPage = customerService.getCustomerList(keyword, pageable);
        model.addAttribute("customerPage", customerPage);
        model.addAttribute("keyword", keyword);
        return "admin/pages/customerlist";
    }

    @GetMapping("/customers/status")
    public String customerStatus(@RequestParam Long id, Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            User customer = customerService.getCustomerById(id);
            model.addAttribute("customer", customer);
            return "admin/pages/customerstatus";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/customers";
        }
    }

    @PostMapping("/customers/status")
    public String customerToggle(@RequestParam Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            customerService.toggleCustomerStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng.");
        }
        return "redirect:/admin/customers";
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
}
