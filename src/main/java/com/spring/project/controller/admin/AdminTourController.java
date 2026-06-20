package com.spring.project.controller.admin;

import com.spring.project.dto.DestinationRequest;
import com.spring.project.dto.TourCategoryRequest;
import com.spring.project.dto.TourCreateRequest;
import com.spring.project.dto.TourDepartureRequest;
import com.spring.project.dto.TourUpdateRequest;
import com.spring.project.entity.Destination;
import com.spring.project.entity.Tour;
import com.spring.project.entity.TourCategory;
import com.spring.project.entity.TourDeparture;
import com.spring.project.repository.DestinationRepository;
import com.spring.project.repository.TourCategoryRepository;
import com.spring.project.service.DestinationService;
import com.spring.project.service.TourCategoryService;
import com.spring.project.service.TourDepartureService;
import com.spring.project.service.TourService;
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

import java.util.List;

/**
 * Quản lý Tour, Lịch khởi hành, Danh mục, Điểm đến cho khu vực Admin.
 */
@Controller
@RequestMapping("/admin")
public class AdminTourController {

    private final TourService tourService;
    private final TourDepartureService tourDepartureService;
    private final TourCategoryService tourCategoryService;
    private final DestinationService destinationService;
    private final TourCategoryRepository tourCategoryRepository;
    private final DestinationRepository destinationRepository;

    public AdminTourController(TourService tourService,
                               TourDepartureService tourDepartureService,
                               TourCategoryService tourCategoryService,
                               DestinationService destinationService,
                               TourCategoryRepository tourCategoryRepository,
                               DestinationRepository destinationRepository) {
        this.tourService = tourService;
        this.tourDepartureService = tourDepartureService;
        this.tourCategoryService = tourCategoryService;
        this.destinationService = destinationService;
        this.tourCategoryRepository = tourCategoryRepository;
        this.destinationRepository = destinationRepository;
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
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/tourcreate";
        }
        try {
            tourService.createTour(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo tour thành công!");
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
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại.");
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
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/tourupdate";
        }
        try {
            tourService.updateTour(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tour thành công!");
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
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại.");
            return "redirect:/admin/tours";
        }
    }

    @PostMapping("/tours/delete")
    public String tourDeletePost(@RequestParam Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            tourService.deleteTour(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tour đã được xóa!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại.");
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
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại.");
            return "redirect:/admin/tours";
        }
    }

    @PostMapping("/tours/departures/add")
    public String departureAdd(@Valid @ModelAttribute("departureRequest") TourDepartureRequest request,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/tours/departures?tourId=" + request.getTourId();
        }
        try {
            tourDepartureService.addDeparture(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm chuyến thành công!");
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
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chuyến thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tours/departures?tourId=" + tourId;
    }

    // ==================== CATEGORY MANAGEMENT ====================

    @GetMapping("/categories")
    public String categoryList(@RequestParam(required = false) String status,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<TourCategory> categoryPage = tourCategoryService.getCategoryList(status, pageable);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("status", status);
        return "admin/pages/categorylist";
    }

    @GetMapping("/categories/create")
    public String categoryCreate(Model model) {
        if (!model.containsAttribute("categoryRequest")) {
            model.addAttribute("categoryRequest", new TourCategoryRequest());
        }
        return "admin/pages/categorycreate";
    }

    @PostMapping("/categories/create")
    public String categoryCreatePost(@Valid @ModelAttribute("categoryRequest") TourCategoryRequest request,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/categorycreate";
        }
        try {
            tourCategoryService.createCategory(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/pages/categorycreate";
        }
    }

    @GetMapping("/categories/update")
    public String categoryUpdate(@RequestParam Long id, Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            TourCategory category = tourCategoryService.getCategoryById(id);
            model.addAttribute("category", category);
            if (!model.containsAttribute("categoryRequest")) {
                TourCategoryRequest req = new TourCategoryRequest();
                req.setName(category.getName());
                req.setDescription(category.getDescription());
                req.setStatus(category.getStatus());
                model.addAttribute("categoryRequest", req);
            }
            return "admin/pages/categoryupdate";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @PostMapping("/categories/update")
    public String categoryUpdatePost(@RequestParam Long id,
                                      @Valid @ModelAttribute("categoryRequest") TourCategoryRequest request,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("category", tourCategoryService.getCategoryById(id));
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/categoryupdate";
        }
        try {
            tourCategoryService.updateCategory(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories/update?id=" + id;
        }
    }

    @GetMapping("/categories/delete")
    public String categoryDelete(@RequestParam Long id, Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            TourCategory category = tourCategoryService.getCategoryById(id);
            model.addAttribute("category", category);
            model.addAttribute("tourCount", tourCategoryService.countTours(id));
            return "admin/pages/categorydelete";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @PostMapping("/categories/delete")
    public String categoryDeletePost(@RequestParam Long id,
                                      RedirectAttributes redirectAttributes) {
        try {
            tourCategoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xử lý xóa danh mục.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // ==================== DESTINATION MANAGEMENT ====================

    @GetMapping("/destinations")
    public String destinationList(@RequestParam(required = false) String status,
                                  @RequestParam(defaultValue = "0") int page,
                                  Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Destination> destinationPage = destinationService.getDestinationList(status, pageable);
        model.addAttribute("destinationPage", destinationPage);
        model.addAttribute("status", status);
        return "admin/pages/destinationlist";
    }

    @GetMapping("/destinations/create")
    public String destinationCreate(Model model) {
        if (!model.containsAttribute("destinationRequest")) {
            DestinationRequest req = new DestinationRequest();
            req.setCountry("Việt Nam");
            model.addAttribute("destinationRequest", req);
        }
        return "admin/pages/destinationcreate";
    }

    @PostMapping("/destinations/create")
    public String destinationCreatePost(@Valid @ModelAttribute("destinationRequest") DestinationRequest request,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/destinationcreate";
        }
        try {
            destinationService.createDestination(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo điểm đến thành công!");
            return "redirect:/admin/destinations";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/pages/destinationcreate";
        }
    }

    @GetMapping("/destinations/update")
    public String destinationUpdate(@RequestParam Long id, Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            Destination destination = destinationService.getDestinationById(id);
            model.addAttribute("destination", destination);
            if (!model.containsAttribute("destinationRequest")) {
                DestinationRequest req = new DestinationRequest();
                req.setName(destination.getName());
                req.setProvince(destination.getProvince());
                req.setCountry(destination.getCountry());
                req.setDescription(destination.getDescription());
                req.setImageUrl(destination.getImageUrl());
                req.setStatus(destination.getStatus());
                model.addAttribute("destinationRequest", req);
            }
            return "admin/pages/destinationupdate";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/destinations";
        }
    }

    @PostMapping("/destinations/update")
    public String destinationUpdatePost(@RequestParam Long id,
                                         @Valid @ModelAttribute("destinationRequest") DestinationRequest request,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("destination", destinationService.getDestinationById(id));
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/destinationupdate";
        }
        try {
            destinationService.updateDestination(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật điểm đến thành công!");
            return "redirect:/admin/destinations";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/destinations/update?id=" + id;
        }
    }

    @GetMapping("/destinations/delete")
    public String destinationDelete(@RequestParam Long id, Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            Destination destination = destinationService.getDestinationById(id);
            model.addAttribute("destination", destination);
            model.addAttribute("tourCount", destinationService.countTours(id));
            return "admin/pages/destinationdelete";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/destinations";
        }
    }

    @PostMapping("/destinations/delete")
    public String destinationDeletePost(@RequestParam Long id,
                                         RedirectAttributes redirectAttributes) {
        try {
            destinationService.deleteDestination(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xử lý xóa điểm đến.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/destinations";
    }
}
