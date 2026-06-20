package com.spring.project.controller.client;

import com.spring.project.dto.RegisterRequest;
import com.spring.project.repository.UserRepository;
import com.spring.project.service.AuthService;
import com.spring.project.service.EmailService;
import com.spring.project.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Đăng nhập, đăng ký (kèm OTP qua email), quên mật khẩu cho khách hàng.
 */
@Controller
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService,
                          UserService userService,
                          EmailService emailService,
                          UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    // ==================== LOGIN ====================

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String registered,
            HttpSession session,
            Model model) {

        session.setAttribute("loginSource", "client");

        if (error != null)
            model.addAttribute("errorMessage", "Email hoặc mật khẩu không đúng");
        if (logout != null)
            model.addAttribute("logoutMessage", "Đã đăng xuất thành công");
        if (registered != null)
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "client/pages/login";
    }

    // POST /login do Spring Security tự xử lý

    // ==================== REGISTER ====================

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "client/pages/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "client/pages/register";
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            model.addAttribute("errorMessage", "Email này đã được sử dụng");
            return "client/pages/register";
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (userRepository.existsByPhone(request.getPhone())) {
                model.addAttribute("errorMessage", "Số điện thoại này đã được sử dụng");
                return "client/pages/register";
            }
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp");
            return "client/pages/register";
        }

        // Sinh OTP 6 chữ số, lưu vào session
        String otp = String.format("%06d", new Random().nextInt(1000000));
        session.setAttribute("pendingRegister", request);
        session.setAttribute("registerOtp", otp);
        session.setAttribute("registerOtpExpiry", System.currentTimeMillis() + 5 * 60 * 1000);

        emailService.sendVerificationOtp(request.getEmail(), otp);

        return "redirect:/register/verify";
    }

    @GetMapping("/register/verify")
    public String registerVerify(HttpSession session, Model model) {
        RegisterRequest pending = (RegisterRequest) session.getAttribute("pendingRegister");
        if (pending == null) {
            return "redirect:/register";
        }
        model.addAttribute("maskedEmail", maskEmail(pending.getEmail()));
        return "client/pages/registerverify";
    }

    @PostMapping("/register/verify")
    public String registerVerifyPost(@RequestParam String otp,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        RegisterRequest pending = (RegisterRequest) session.getAttribute("pendingRegister");
        String savedOtp = (String) session.getAttribute("registerOtp");
        Long expiry = (Long) session.getAttribute("registerOtpExpiry");

        if (pending == null || savedOtp == null || expiry == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng ký đã hết hạn. Vui lòng đăng ký lại.");
            return "redirect:/register";
        }

        if (System.currentTimeMillis() > expiry) {
            model.addAttribute("maskedEmail", maskEmail(pending.getEmail()));
            model.addAttribute("errorMessage", "Mã xác thực đã hết hạn. Vui lòng nhấn Gửi lại mã.");
            return "client/pages/registerverify";
        }

        if (!savedOtp.equals(otp.trim())) {
            model.addAttribute("maskedEmail", maskEmail(pending.getEmail()));
            model.addAttribute("errorMessage", "Mã xác thực không chính xác.");
            return "client/pages/registerverify";
        }

        try {
            authService.register(pending);
            session.removeAttribute("pendingRegister");
            session.removeAttribute("registerOtp");
            session.removeAttribute("registerOtpExpiry");

            redirectAttributes.addFlashAttribute("successMessage",
                    "Xác thực thành công! Đăng ký hoàn tất. Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("maskedEmail", maskEmail(pending.getEmail()));
            model.addAttribute("errorMessage", e.getMessage());
            return "client/pages/registerverify";
        }
    }

    @PostMapping("/register/resend-otp")
    public String resendOtp(HttpSession session, RedirectAttributes redirectAttributes) {
        RegisterRequest pending = (RegisterRequest) session.getAttribute("pendingRegister");
        if (pending == null) {
            return "redirect:/register";
        }

        String newOtp = String.format("%06d", new Random().nextInt(1000000));
        session.setAttribute("registerOtp", newOtp);
        session.setAttribute("registerOtpExpiry", System.currentTimeMillis() + 5 * 60 * 1000);

        emailService.sendVerificationOtp(pending.getEmail(), newOtp);

        redirectAttributes.addFlashAttribute("successMessage", "Đã gửi lại mã xác thực. Vui lòng kiểm tra email.");
        return "redirect:/register/verify";
    }

    // ==================== FORGOT PASSWORD ====================

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("email", "");
        return "client/pages/forgotpassword";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordPost(@RequestParam String email,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            userService.assertCanResetPassword(email.trim());

            String newPassword = generateRandomPassword();
            userService.resetPassword(email.trim(), newPassword);
            emailService.sendNewPassword(email.trim(), newPassword);

            session.setAttribute("justResetPasswordEmail", email.trim());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Mật khẩu mới đã được gửi tới email của bạn. Vui lòng kiểm tra hộp thư.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("email", email);
            return "client/pages/forgotpassword";
        }
    }

    // ==================== HELPERS ====================

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 3) {
            return email.charAt(0) + "***" + email.substring(atIndex);
        }
        return email.substring(0, 3) + "***" + email.substring(atIndex);
    }
}
