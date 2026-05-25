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
import com.spring.project.service.PaymentService;
import com.spring.project.service.TourService;
import com.spring.project.service.UserService;
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

    public UserController(AuthService authService, UserService userService,
                         TourService tourService,
                         BookingService bookingService,
                         PaymentService paymentService,
                         TourCategoryRepository tourCategoryRepository,
                         DestinationRepository destinationRepository,
                         TourDepartureRepository tourDepartureRepository,
                         ReviewRepository reviewRepository) {
        this.authService = authService;
        this.userService = userService;
        this.tourService = tourService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.tourCategoryRepository = tourCategoryRepository;
        this.destinationRepository = destinationRepository;
        this.tourDepartureRepository = tourDepartureRepository;
        this.reviewRepository = reviewRepository;
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
    public String tourList(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Long destinationId,
                           @RequestParam(required = false) Long categoryId,
                           @RequestParam(required = false) Integer minDuration,
                           @RequestParam(required = false) Integer maxDuration,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        Pageable pageable = PageRequest.of(page, 9, Sort.by("createdAt").descending());
        Page<Tour> tourPage = tourService.searchToursForClient(
                keyword, destinationId, categoryId, minDuration, maxDuration, pageable);
        model.addAttribute("tourPage", tourPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("destinationId", destinationId);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minDuration", minDuration);
        model.addAttribute("maxDuration", maxDuration);
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
            com.spring.project.entity.Payment payment = paymentService.processPayment(bookingId, userId, paymentMethod, note);
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
