package com.spring.project.controller;

import com.spring.project.dto.ChangePasswordRequest;
import com.spring.project.dto.RegisterRequest;
import com.spring.project.dto.TourCreateRequest;
import com.spring.project.dto.TourDepartureRequest;
import com.spring.project.dto.TourUpdateRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.Tour;
import com.spring.project.entity.TourDeparture;
import com.spring.project.entity.User;
import com.spring.project.repository.DestinationRepository;
import com.spring.project.repository.TourCategoryRepository;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.AdminDashboardService;
import com.spring.project.service.AuthService;
import com.spring.project.service.StaffService;
import com.spring.project.service.TourDepartureService;
import com.spring.project.service.TourService;
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

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AdminDashboardService adminDashboardService;
    private final AuthService authService;
    private final StaffService staffService;
    private final TourService tourService;
    private final TourDepartureService tourDepartureService;
    private final TourCategoryRepository tourCategoryRepository;
    private final DestinationRepository destinationRepository;
    private final com.spring.project.service.BookingService bookingService;
    private final com.spring.project.service.PromotionService promotionService;
    private final com.spring.project.service.CustomerService customerService;

    public AdminController(UserService userService, AdminDashboardService adminDashboardService,
                           AuthService authService, StaffService staffService,
                           TourService tourService, TourDepartureService tourDepartureService,
                           TourCategoryRepository tourCategoryRepository, DestinationRepository destinationRepository,
                           com.spring.project.service.BookingService bookingService,
                           com.spring.project.service.PromotionService promotionService,
                           com.spring.project.service.CustomerService customerService) {
        this.userService = userService;
        this.adminDashboardService = adminDashboardService;
        this.authService = authService;
        this.staffService = staffService;
        this.tourService = tourService;
        this.tourDepartureService = tourDepartureService;
        this.tourCategoryRepository = tourCategoryRepository;
        this.destinationRepository = destinationRepository;
        this.bookingService = bookingService;
        this.promotionService = promotionService;
        this.customerService = customerService;
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
    public String dashboard(Model model) {
        model.addAttribute("dashboard", adminDashboardService.getDashboard());
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
    public String tourList(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String status,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Tour> tourPage = tourService.getTourList(keyword, status, pageable);
        model.addAttribute("tourPage", tourPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("categories", tourCategoryRepository.findAll());
        model.addAttribute("destinations", destinationRepository.findAll());
        return "admin/pages/tourlist";
    }

    @GetMapping("/tours/create")
    public String tourCreate(Model model) {
        if (!model.containsAttribute("tourCreateRequest")) {
            model.addAttribute("tourCreateRequest", new TourCreateRequest());
        }
        model.addAttribute("categories", tourCategoryRepository.findAll());
        model.addAttribute("destinations", destinationRepository.findAll());
        return "admin/pages/tourcreate";
    }

    @PostMapping("/tours/create")
    public String tourCreatePost(@Valid @ModelAttribute("tourCreateRequest") TourCreateRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", tourCategoryRepository.findAll());
            model.addAttribute("destinations", destinationRepository.findAll());
            model.addAttribute("errorMessage", "Vui l\u00f2ng ki\u1ec3m tra l\u1ea1i th\u00f4ng tin");
            return "admin/pages/tourcreate";
        }
        try {
            tourService.createTour(request);
            redirectAttributes.addFlashAttribute("successMessage", "T\u1ea1o tour th\u00e0nh c\u00f4ng!");
            return "redirect:/admin/tours";
        } catch (IllegalArgumentException e) {
            model.addAttribute("categories", tourCategoryRepository.findAll());
            model.addAttribute("destinations", destinationRepository.findAll());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/pages/tourcreate";
        }
    }

    @GetMapping("/tours/update")
    public String tourUpdate(@RequestParam Long id, Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            Tour tour = tourService.getTourById(id);
            model.addAttribute("tour", tour);
            if (!model.containsAttribute("tourUpdateRequest")) {
                TourUpdateRequest req = new TourUpdateRequest();
                req.setCategoryId(tour.getCategory().getId());
                req.setDestinationId(tour.getDestination().getId());
                req.setName(tour.getName());
                req.setSlug(tour.getSlug());
                req.setDepartureLocation(tour.getDepartureLocation());
                req.setDurationDays(tour.getDurationDays());
                req.setDurationNights(tour.getDurationNights());
                req.setTransport(tour.getTransport());
                req.setHotelStandard(tour.getHotelStandard());
                req.setDescription(tour.getDescription());
                req.setPolicy(tour.getPolicy());
                req.setIncludedServices(tour.getIncludedServices());
                req.setExcludedServices(tour.getExcludedServices());
                req.setNotes(tour.getNotes());
                req.setStatus(tour.getStatus());
                model.addAttribute("tourUpdateRequest", req);
            }
            model.addAttribute("categories", tourCategoryRepository.findAll());
            model.addAttribute("destinations", destinationRepository.findAll());
            return "admin/pages/tourupdate";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour kh\u00f4ng t\u1ed3n t\u1ea1i.");
            return "redirect:/admin/tours";
        }
    }

    @PostMapping("/tours/update")
    public String tourUpdatePost(@RequestParam Long id,
                                 @Valid @ModelAttribute("tourUpdateRequest") TourUpdateRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tour", tourService.getTourById(id));
            model.addAttribute("categories", tourCategoryRepository.findAll());
            model.addAttribute("destinations", destinationRepository.findAll());
            model.addAttribute("errorMessage", "Vui l\u00f2ng ki\u1ec3m tra l\u1ea1i th\u00f4ng tin");
            return "admin/pages/tourupdate";
        }
        try {
            tourService.updateTour(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "C\u1eadp nh\u1eadt tour th\u00e0nh c\u00f4ng!");
            return "redirect:/admin/tours";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/tours/update?id=" + id;
        }
    }

    @GetMapping("/tours/delete")
    public String tourDelete(@RequestParam Long id, Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            Tour tour = tourService.getTourById(id);
            model.addAttribute("tour", tour);
            return "admin/pages/tourdelete";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour kh\u00f4ng t\u1ed3n t\u1ea1i.");
            return "redirect:/admin/tours";
        }
    }

    @PostMapping("/tours/delete")
    public String tourDeletePost(@RequestParam Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            tourService.deleteTour(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tour \u0111\u00e3 \u0111\u01b0\u1ee3c x\u00f3a!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour kh\u00f4ng t\u1ed3n t\u1ea1i.");
        }
        return "redirect:/admin/tours";
    }

    // ==================== DEPARTURE MANAGEMENT ====================

    @GetMapping("/tours/departures")
    public String tourDepartures(@RequestParam Long tourId, Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            Tour tour = tourService.getTourById(tourId);
            List<TourDeparture> departures = tourDepartureService.getDeparturesByTourId(tourId);
            model.addAttribute("tour", tour);
            model.addAttribute("departures", departures);
            if (!model.containsAttribute("departureRequest")) {
                TourDepartureRequest req = new TourDepartureRequest();
                req.setTourId(tourId);
                model.addAttribute("departureRequest", req);
            }
            return "admin/pages/tourdepartures";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour kh\u00f4ng t\u1ed3n t\u1ea1i.");
            return "redirect:/admin/tours";
        }
    }

    @PostMapping("/tours/departures/add")
    public String departureAdd(@Valid @ModelAttribute("departureRequest") TourDepartureRequest request,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui l\u00f2ng ki\u1ec3m tra l\u1ea1i th\u00f4ng tin");
            return "redirect:/admin/tours/departures?tourId=" + request.getTourId();
        }
        try {
            tourDepartureService.addDeparture(request);
            redirectAttributes.addFlashAttribute("successMessage", "Th\u00eam chuy\u1ebfn th\u00e0nh c\u00f4ng!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tours/departures?tourId=" + request.getTourId();
    }

    @PostMapping("/tours/departures/delete")
    public String departureDelete(@RequestParam Long id, @RequestParam Long tourId,
                                  RedirectAttributes redirectAttributes) {
        try {
            tourDepartureService.deleteDeparture(id);
            redirectAttributes.addFlashAttribute("successMessage", "X\u00f3a chuy\u1ebfn th\u00e0nh c\u00f4ng!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tours/departures?tourId=" + tourId;
    }

    // ==================== BOOKING MANAGEMENT ====================

    @GetMapping("/bookings")
    public String bookingList(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String status,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<com.spring.project.entity.Booking> bookingPage = bookingService.getBookingList(keyword, status, pageable);
        model.addAttribute("bookingPage", bookingPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "admin/pages/bookinglist";
    }

    @GetMapping("/bookings/status")
    public String bookingStatusUpdate(@RequestParam Long id, Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            com.spring.project.entity.Booking booking = bookingService.getBookingById(id);
            model.addAttribute("booking", booking);
            model.addAttribute("allowedStatuses", bookingService.getAllowedTransitions(booking.getBookingStatus()));
            return "admin/pages/bookingstatusupdate";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
            return "redirect:/admin/bookings";
        }
    }

    @PostMapping("/bookings/status")
    public String bookingStatusUpdatePost(@RequestParam Long id,
                                           @RequestParam String newStatus,
                                           RedirectAttributes redirectAttributes) {
        try {
            bookingService.updateBookingStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
        }
        return "redirect:/admin/bookings";
    }

    @GetMapping("/bookings/delete")
    public String bookingDelete(@RequestParam Long id, Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            com.spring.project.entity.Booking booking = bookingService.getBookingById(id);
            model.addAttribute("booking", booking);
            return "admin/pages/bookingdelete";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
            return "redirect:/admin/bookings";
        }
    }

    @PostMapping("/bookings/delete")
    public String bookingDeletePost(@RequestParam Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            bookingService.deleteBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa đơn đặt thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
        }
        return "redirect:/admin/bookings";
    }

    // ==================== PROMOTION MANAGEMENT ====================

    @GetMapping("/promotions")
    public String promotionList(@RequestParam(required = false) String status,
                                @RequestParam(defaultValue = "0") int page,
                                Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<com.spring.project.entity.Promotion> promotionPage = promotionService.getPromotionList(status, pageable);
        model.addAttribute("promotionPage", promotionPage);
        model.addAttribute("status", status);
        return "admin/pages/promotionlist";
    }

    @GetMapping("/promotions/create")
    public String promotionCreate(Model model) {
        if (!model.containsAttribute("promotionRequest")) {
            model.addAttribute("promotionRequest", new com.spring.project.dto.PromotionRequest());
        }
        return "admin/pages/promotioncreate";
    }

    @PostMapping("/promotions/create")
    public String promotionCreatePost(@Valid @ModelAttribute("promotionRequest") com.spring.project.dto.PromotionRequest request,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/promotioncreate";
        }
        try {
            promotionService.createPromotion(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo khuyến mãi thành công!");
            return "redirect:/admin/promotions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/pages/promotioncreate";
        }
    }

    @GetMapping("/promotions/update")
    public String promotionUpdate(@RequestParam Long id, Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            com.spring.project.entity.Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
            if (!model.containsAttribute("promotionRequest")) {
                com.spring.project.dto.PromotionRequest req = new com.spring.project.dto.PromotionRequest();
                req.setCode(promotion.getCode());
                req.setName(promotion.getName());
                req.setDescription(promotion.getDescription());
                req.setDiscountType(promotion.getDiscountType());
                req.setDiscountValue(promotion.getDiscountValue());
                req.setMaxDiscountAmount(promotion.getMaxDiscountAmount());
                req.setMinBookingAmount(promotion.getMinBookingAmount());
                req.setStartDate(promotion.getStartDate());
                req.setEndDate(promotion.getEndDate());
                req.setUsageLimit(promotion.getUsageLimit());
                req.setStatus(promotion.getStatus());
                model.addAttribute("promotionRequest", req);
            }
            return "admin/pages/promotionupdate";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khuyến mãi không tồn tại.");
            return "redirect:/admin/promotions";
        }
    }

    @PostMapping("/promotions/update")
    public String promotionUpdatePost(@RequestParam Long id,
                                       @Valid @ModelAttribute("promotionRequest") com.spring.project.dto.PromotionRequest request,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("promotion", promotionService.getPromotionById(id));
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/promotionupdate";
        }
        try {
            promotionService.updatePromotion(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khuyến mãi thành công!");
            return "redirect:/admin/promotions";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/promotions/update?id=" + id;
        }
    }

    @GetMapping("/promotions/delete")
    public String promotionDelete(@RequestParam Long id, Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            com.spring.project.entity.Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
            return "admin/pages/promotiondelete";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khuyến mãi không tồn tại.");
            return "redirect:/admin/promotions";
        }
    }

    @PostMapping("/promotions/delete")
    public String promotionDeletePost(@RequestParam Long id,
                                       RedirectAttributes redirectAttributes) {
        try {
            promotionService.deletePromotion(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khuyến mãi thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khuyến mãi không tồn tại.");
        }
        return "redirect:/admin/promotions";
    }

    // ==================== CUSTOMER MANAGEMENT ====================

    @GetMapping("/customers")
    public String customerList(@RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<com.spring.project.entity.User> customerPage = customerService.getCustomerList(keyword, pageable);
        model.addAttribute("customerPage", customerPage);
        model.addAttribute("keyword", keyword);
        return "admin/pages/customerlist";
    }

    @GetMapping("/customers/status")
    public String customerStatus(@RequestParam Long id, Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            com.spring.project.entity.User customer = customerService.getCustomerById(id);
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
