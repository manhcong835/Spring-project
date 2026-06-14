package com.spring.project.controller;

import com.spring.project.dto.BookingCreateRequest;
import com.spring.project.dto.ChangePasswordRequest;
import com.spring.project.dto.RegisterRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.Booking;
import com.spring.project.entity.Tour;
import com.spring.project.entity.TourCategory;
import com.spring.project.entity.Destination;
import com.spring.project.entity.User;
import com.spring.project.repository.TourCategoryRepository;
import com.spring.project.repository.TourDepartureRepository;
import com.spring.project.repository.ReviewRepository;
import com.spring.project.repository.DestinationRepository;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.AuthService;
import com.spring.project.service.BookingService;
import com.spring.project.service.EmailService;
import com.spring.project.service.PaymentService;
import com.spring.project.service.ReviewService;
import com.spring.project.service.TourService;
import com.spring.project.service.UserService;
import com.spring.project.repository.UserRepository;
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

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final TourService tourService;
    private final BookingService bookingService;
    private final TourCategoryRepository tourCategoryRepository;
    private final DestinationRepository destinationRepository;
    private final TourDepartureRepository tourDepartureRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentService paymentService;
    private final ReviewService reviewService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public UserController(AuthService authService, UserService userService,
            TourService tourService,
            BookingService bookingService,
            PaymentService paymentService,
            ReviewService reviewService,
            EmailService emailService,
            TourCategoryRepository tourCategoryRepository,
            DestinationRepository destinationRepository,
            TourDepartureRepository tourDepartureRepository,
            ReviewRepository reviewRepository,
            UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.tourService = tourService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.reviewService = reviewService;
        this.emailService = emailService;
        this.tourCategoryRepository = tourCategoryRepository;
        this.destinationRepository = destinationRepository;
        this.tourDepartureRepository = tourDepartureRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    // ==================== TRANG CHỦ ====================
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping({ "/", "/home" })
    public String home(Model model) {
        // Tour nổi bật (4 tour mới nhất)
        Pageable hotPageable = PageRequest.of(0, 4, Sort.by("id").descending());
        Page<Tour> hotPage = tourService.searchToursForClient(null, null, null, null, null, null, null, hotPageable);
        model.addAttribute("hotTours", hotPage.getContent());

        // Tour khuyến mãi (3 tour cũ nhất)
        Pageable promoPageable = PageRequest.of(0, 3, Sort.by("id").ascending());
        Page<Tour> promoPage = tourService.searchToursForClient(null, null, null, null, null, null, null, promoPageable);
        model.addAttribute("promoTours", promoPage.getContent());

        // Điểm đến phổ biến
        model.addAttribute("destinations", destinationRepository.findByStatus("ACTIVE"));

        // Đánh giá gần nhất
        Pageable reviewPageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        model.addAttribute("reviews", reviewRepository.findRecentVisibleReviews(reviewPageable).getContent());

        return "client/pages/home";
    }

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
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Nếu có lỗi validation (annotation trên DTO)
        if (bindingResult.hasErrors()) {
            return "client/pages/register";
        }

        // Kiểm tra trùng email/phone trước khi gửi OTP
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
        String otp = String.format("%06d", new java.util.Random().nextInt(1000000));
        session.setAttribute("pendingRegister", request);
        session.setAttribute("registerOtp", otp);
        session.setAttribute("registerOtpExpiry", System.currentTimeMillis() + 5 * 60 * 1000); // 5 phút

        // Gửi OTP qua email
        emailService.sendVerificationOtp(request.getEmail(), otp);

        return "redirect:/register/verify";
    }

    @GetMapping("/register/verify")
    public String registerVerify(HttpSession session, Model model) {
        RegisterRequest pending = (RegisterRequest) session.getAttribute("pendingRegister");
        if (pending == null) {
            return "redirect:/register";
        }
        // Che email: ngu***@gmail.com
        String email = pending.getEmail();
        String maskedEmail = maskEmail(email);
        model.addAttribute("maskedEmail", maskedEmail);
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

        // Kiểm tra hết hạn
        if (System.currentTimeMillis() > expiry) {
            model.addAttribute("maskedEmail", maskEmail(pending.getEmail()));
            model.addAttribute("errorMessage", "Mã xác thực đã hết hạn. Vui lòng nhấn Gửi lại mã.");
            return "client/pages/registerverify";
        }

        // Kiểm tra OTP
        if (!savedOtp.equals(otp.trim())) {
            model.addAttribute("maskedEmail", maskEmail(pending.getEmail()));
            model.addAttribute("errorMessage", "Mã xác thực không chính xác.");
            return "client/pages/registerverify";
        }

        // OTP đúng -> lưu user vào DB
        try {
            authService.register(pending);
            // Xóa session tạm
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

        // Sinh OTP mới
        String newOtp = String.format("%06d", new java.util.Random().nextInt(1000000));
        session.setAttribute("registerOtp", newOtp);
        session.setAttribute("registerOtpExpiry", System.currentTimeMillis() + 5 * 60 * 1000);

        emailService.sendVerificationOtp(pending.getEmail(), newOtp);

        redirectAttributes.addFlashAttribute("successMessage", "Đã gửi lại mã xác thực. Vui lòng kiểm tra email.");
        return "redirect:/register/verify";
    }

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
            // Chặn spam: 1 email chỉ được request reset 2 phút/lần.
            userService.assertCanResetPassword(email.trim());

            String newPassword = generateRandomPassword();
            userService.resetPassword(email.trim(), newPassword);
            emailService.sendNewPassword(email.trim(), newPassword);

            // Đánh dấu email vừa reset để SuccessHandler chuyển hướng đổi mật khẩu
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

    // ==================== HELPER METHODS ====================

    /** Sinh mật khẩu ngẫu nhiên 8 ký tự (chữ hoa, chữ thường, số) */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /** Che email: nguyen@gmail.com → ngu***@gmail.com */
    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 3) {
            return email.charAt(0) + "***" + email.substring(atIndex);
        }
        return email.substring(0, 3) + "***" + email.substring(atIndex);
    }

    // ==================== PROFILE ====================

    @GetMapping("/profile")
    public String profile(@RequestParam(required = false) String success,
            @RequestParam(required = false) String passwordChanged,
            @RequestParam(required = false) String forceChange,
            HttpSession session,
            Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userService.getUserById(userId);
        boolean hasLocalProvider = userService.hasLocalProvider(userId);

        model.addAttribute("user", user);
        model.addAttribute("hasLocalProvider", hasLocalProvider);

        // Kiểm tra cờ session: vừa reset mật khẩu → ép đổi mật khẩu mới
        String justReset = (String) session.getAttribute("justResetPasswordEmail");
        boolean forceChangeFlag = justReset != null && justReset.equalsIgnoreCase(user.getEmail());
        model.addAttribute("forceChange", forceChangeFlag);

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
        if (success != null)
            model.addAttribute("successMessage", "Cập nhật thông tin thành công!");
        if (passwordChanged != null)
            model.addAttribute("successMessage", "Đổi mật khẩu thành công!");

        return "client/pages/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateProfileRequest",
                    bindingResult);
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
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra cờ session: vừa reset mật khẩu → bypass kiểm tra mật khẩu cũ
        String justReset = (String) session.getAttribute("justResetPasswordEmail");
        Long userId = SecurityUtils.getCurrentUserId();
        User currentUser = userService.getUserById(userId);

        if (justReset != null && justReset.equalsIgnoreCase(currentUser.getEmail())) {
            // Chỉ validate mật khẩu mới
            if (bindingResult.hasFieldErrors("newPassword") || bindingResult.hasFieldErrors("confirmNewPassword")) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordRequest",
                        bindingResult);
                redirectAttributes.addFlashAttribute("changePasswordRequest", request);
                redirectAttributes.addFlashAttribute("passwordError", "Vui lòng kiểm tra lại thông tin");
                return "redirect:/profile?forceChange=true";
            }
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu xác nhận không khớp");
                redirectAttributes.addFlashAttribute("changePasswordRequest", request);
                return "redirect:/profile?forceChange=true";
            }

            // Cập nhật trực tiếp không cần kiểm tra mật khẩu cũ
            userService.resetPassword(currentUser.getEmail(), request.getNewPassword());
            session.removeAttribute("justResetPasswordEmail");
            return "redirect:/profile?passwordChanged=true";
        }

        // Luồng bình thường: đổi mật khẩu có kiểm tra mật khẩu cũ
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordRequest",
                    bindingResult);
            redirectAttributes.addFlashAttribute("changePasswordRequest", request);
            redirectAttributes.addFlashAttribute("passwordError", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/profile";
        }

        try {
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
    public String tourList(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Pageable pageable = PageRequest.of(page, 9, Sort.by("createdAt").descending());
        Page<Tour> tourPage = tourService.searchToursForClient(
                keyword, destinationId, categoryId, minDuration, maxDuration, minPrice, maxPrice, pageable);
        model.addAttribute("tourPage", tourPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("destinationId", destinationId);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minDuration", minDuration);
        model.addAttribute("maxDuration", maxDuration);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("categories", tourCategoryRepository.findAll());
        model.addAttribute("destinations", destinationRepository.findAll());
        return "client/pages/tourlist";
    }

    @GetMapping("/tours/detail")
    public String tourDetail(@RequestParam Long id, Model model, RedirectAttributes redirectAttributes) {
        Tour tour = tourService.getTourDetailForClient(id);
        if (tour == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại hoặc đã ngừng hoạt động");
            return "redirect:/tours";
        }

        // Load departures còn slot (riêng để tránh MultipleBagFetchException)
        var departures = tourDepartureRepository.findAvailableDeparturesByTour(id, java.time.LocalDate.now());

        // Load reviews (VISIBLE only)
        var reviews = reviewRepository.findByTourIdAndStatusOrderByCreatedAtDesc(id, "VISIBLE");
        Double avgRating = reviewRepository.calculateAverageRatingByTourId(id);
        Long reviewCount = reviewRepository.countVisibleReviewsByTourId(id);

        model.addAttribute("tour", tour);
        model.addAttribute("departures", departures);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avgRating != null ? String.format("%.1f", avgRating) : "0");
        model.addAttribute("reviewCount", reviewCount != null ? reviewCount : 0);

        return "client/pages/tourdetail";
    }

    // ==================== BOOKING ====================

    @GetMapping("/booking/create")
    public String bookingCreate(@RequestParam Long tourId,
            @RequestParam(required = false) Long departureId,
            @RequestParam(required = false) Integer adultCount,
            @RequestParam(required = false) Integer childCount,
            @RequestParam(required = false) Integer infantCount,
            Model model,
            RedirectAttributes redirectAttributes) {
        Tour tour = tourService.getTourDetailForClient(tourId);
        if (tour == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại");
            return "redirect:/tours";
        }

        var departures = tourDepartureRepository.findAvailableDeparturesByTour(tourId, java.time.LocalDate.now());
        if (departures.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour hiện chưa có lịch khởi hành");
            return "redirect:/tours/detail?id=" + tourId;
        }

        // Pre-fill contact info from logged-in user
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userService.getUserById(userId);

        BookingCreateRequest bookingRequest = new BookingCreateRequest();
        bookingRequest.setTourId(tourId);
        bookingRequest.setContactName(user.getFullName());
        bookingRequest.setContactEmail(user.getEmail());
        bookingRequest.setContactPhone(user.getPhone() != null ? user.getPhone() : "");

        if (adultCount != null) {
            bookingRequest.setAdultCount(adultCount);
        }
        if (childCount != null) {
            bookingRequest.setChildCount(childCount);
        }
        if (infantCount != null) {
            bookingRequest.setInfantCount(infantCount);
        }

        // Pre-select departure if specified
        if (departureId != null) {
            bookingRequest.setDepartureId(departureId);
        } else {
            bookingRequest.setDepartureId(departures.get(0).getId());
        }

        model.addAttribute("tour", tour);
        model.addAttribute("departures", departures);
        model.addAttribute("bookingRequest", bookingRequest);

        return "client/pages/bookingcreate";
    }

    @PostMapping("/booking/create")
    public String bookingCreatePost(@Valid @ModelAttribute("bookingRequest") BookingCreateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // Reload tour + departures for re-rendering form
            Tour tour = tourService.getTourDetailForClient(request.getTourId());
            var departures = tourDepartureRepository.findAvailableDeparturesByTour(
                    request.getTourId(), java.time.LocalDate.now());
            model.addAttribute("tour", tour);
            model.addAttribute("departures", departures);
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "client/pages/bookingcreate";
        }

        try {
            Long userId = SecurityUtils.getCurrentUserId();
            Booking booking = bookingService.createBooking(userId, request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đặt tour thành công! Mã đơn: " + booking.getBookingCode());
            return "redirect:/booking/history";
        } catch (IllegalArgumentException e) {
            Tour tour = tourService.getTourDetailForClient(request.getTourId());
            var departures = tourDepartureRepository.findAvailableDeparturesByTour(
                    request.getTourId(), java.time.LocalDate.now());
            model.addAttribute("tour", tour);
            model.addAttribute("departures", departures);
            model.addAttribute("errorMessage", e.getMessage());
            return "client/pages/bookingcreate";
        }
    }

    @GetMapping("/booking/edit")
    public String bookingEdit(@RequestParam Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        Booking booking = bookingService.getBookingById(id);

        if (!booking.getUser().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền sửa đơn này");
            return "redirect:/booking/history";
        }
        if (!"PENDING".equals(booking.getBookingStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ có thể sửa đơn đang Chờ xác nhận");
            return "redirect:/booking/history";
        }

        var departures = tourDepartureRepository.findAvailableDeparturesByTour(
                booking.getTourDeparture().getTour().getId(), java.time.LocalDate.now());

        model.addAttribute("booking", booking);
        model.addAttribute("departures", departures);
        return "client/pages/bookingedit";
    }

    @PostMapping("/booking/edit")
    public String bookingEditPost(@RequestParam Long id,
            @RequestParam Long departureId,
            @RequestParam int adultCount,
            @RequestParam int childCount,
            @RequestParam int infantCount,
            @RequestParam(required = false) String specialRequests,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            bookingService.updateBooking(id, userId, departureId, adultCount, childCount, infantCount, specialRequests);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật đơn đặt tour thành công!");
            return "redirect:/booking/history";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/booking/edit?id=" + id;
        }
    }

    @GetMapping("/booking/cancel")
    public String bookingCancel(@RequestParam Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        Booking booking = bookingService.getBookingById(id);

        if (!booking.getUser().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền hủy đơn này");
            return "redirect:/booking/history";
        }

        String status = booking.getBookingStatus();
        if (!"PENDING".equals(status) && !"CONFIRMED".equals(status)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy đơn ở trạng thái này");
            return "redirect:/booking/history";
        }

        model.addAttribute("booking", booking);
        return "client/pages/bookingcancel";
    }

    @PostMapping("/booking/cancel")
    public String bookingCancelPost(@RequestParam Long id,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            bookingService.cancelBooking(id, userId, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn đặt tour thành công!");
            return "redirect:/booking/history";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/booking/history";
        }
    }

    @GetMapping("/booking/detail")
    public String bookingDetail(@RequestParam Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        Booking booking = bookingService.getBookingById(id);

        if (!booking.getUser().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xem đơn này");
            return "redirect:/booking/history";
        }

        model.addAttribute("booking", booking);
        return "client/pages/bookingdetail";
    }

    @GetMapping("/booking/travelers")
    public String bookingTravelers(@RequestParam Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        Booking booking = bookingService.getBookingById(id);

        if (!booking.getUser().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền cập nhật đơn này");
            return "redirect:/booking/history";
        }
        if (!"PENDING".equals(booking.getBookingStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ có thể cập nhật hành khách khi đơn đang Chờ xác nhận");
            return "redirect:/booking/detail?id=" + id;
        }

        model.addAttribute("booking", booking);
        return "client/pages/bookingtravelers";
    }

    @PostMapping("/booking/travelers")
    public String bookingTravelersPost(@RequestParam Long id,
            @RequestParam("fullName") java.util.List<String> fullNames,
            @RequestParam("travelerType") java.util.List<String> travelerTypes,
            @RequestParam(value = "gender", required = false) java.util.List<String> genders,
            @RequestParam(value = "dateOfBirth", required = false) java.util.List<String> dateOfBirths,
            @RequestParam(value = "identityNumber", required = false) java.util.List<String> identityNumbers,
            @RequestParam(value = "nationality", required = false) java.util.List<String> nationalities,
            @RequestParam(value = "note", required = false) java.util.List<String> notes,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();

            java.util.List<com.spring.project.dto.TravelerInput> travelers = new java.util.ArrayList<>();
            for (int i = 0; i < fullNames.size(); i++) {
                com.spring.project.dto.TravelerInput ti = new com.spring.project.dto.TravelerInput();
                ti.setFullName(fullNames.get(i));
                ti.setTravelerType(travelerTypes.get(i));
                ti.setGender(genders != null && i < genders.size() ? genders.get(i) : null);
                if (dateOfBirths != null && i < dateOfBirths.size() && dateOfBirths.get(i) != null && !dateOfBirths.get(i).isBlank()) {
                    ti.setDateOfBirth(java.time.LocalDate.parse(dateOfBirths.get(i)));
                }
                ti.setIdentityNumber(identityNumbers != null && i < identityNumbers.size() ? identityNumbers.get(i) : null);
                ti.setNationality(nationalities != null && i < nationalities.size() ? nationalities.get(i) : null);
                ti.setNote(notes != null && i < notes.size() ? notes.get(i) : null);
                travelers.add(ti);
            }

            bookingService.updateTravelers(id, userId, travelers);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh sách hành khách thành công!");
            return "redirect:/booking/detail?id=" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/booking/travelers?id=" + id;
        }
    }

    @GetMapping("/booking/history")
    public String bookingHistory(@RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Booking> bookingPage = bookingService.getBookingHistory(userId, status, pageable);

        model.addAttribute("bookingPage", bookingPage);
        model.addAttribute("status", status);
        return "client/pages/bookinghistory";
    }

    // ==================== PAYMENT ====================

    @GetMapping("/payment")
    public String payment(@RequestParam Long bookingId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        Booking booking = bookingService.getBookingById(bookingId);

        // Kiểm tra booking thuộc user
        if (!booking.getUser().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền truy cập đơn này");
            return "redirect:/booking/history";
        }

        // Kiểm tra đã thanh toán
        if ("PAID".equals(booking.getPaymentStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt tour đã được thanh toán");
            return "redirect:/booking/history";
        }

        // Kiểm tra đơn hủy
        if ("CANCELLED".equals(booking.getBookingStatus()) || "DELETED".equals(booking.getBookingStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể thanh toán đơn đã hủy");
            return "redirect:/booking/history";
        }

        model.addAttribute("booking", booking);
        return "client/pages/payment";
    }

    @PostMapping("/payment")
    public String paymentProcess(@RequestParam Long bookingId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            com.spring.project.entity.Payment payment = paymentService.processPayment(bookingId, userId, paymentMethod,
                    note);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thanh toán thành công! Mã giao dịch: " + payment.getPaymentCode());
            return "redirect:/booking/history";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/payment?bookingId=" + bookingId;
        }
    }

    // ==================== REVIEW ====================

    @GetMapping("/review/create")
    public String reviewCreate(@RequestParam Long bookingId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();

        if (!reviewService.canReview(bookingId, userId)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể đánh giá: đơn chưa hoàn thành, không thuộc bạn, hoặc đã đánh giá rồi");
            return "redirect:/booking/history";
        }

        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", booking);
        return "client/pages/reviewcreate";
    }

    @PostMapping("/review/create")
    public String reviewCreatePost(@RequestParam Long bookingId,
            @RequestParam int rating,
            @RequestParam(required = false) String title,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            reviewService.createReview(bookingId, userId, rating, title, content);
            redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn đã đánh giá! ⭐");
            return "redirect:/booking/history";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/review/create?bookingId=" + bookingId;
        }
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
