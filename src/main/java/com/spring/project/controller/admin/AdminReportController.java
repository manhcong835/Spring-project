package com.spring.project.controller.admin;

import com.spring.project.service.RevenueReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Báo cáo doanh thu cho khu vực Admin.
 */
@Controller
@RequestMapping("/admin")
public class AdminReportController {

    private final RevenueReportService revenueReportService;

    public AdminReportController(RevenueReportService revenueReportService) {
        this.revenueReportService = revenueReportService;
    }

    @GetMapping("/revenue")
    public String revenue(@RequestParam(required = false) String from,
                          @RequestParam(required = false) String to,
                          @RequestParam(defaultValue = "day") String groupBy,
                          Model model) {
        LocalDate fromDate = parseDateOrNull(from);
        LocalDate toDate = parseDateOrNull(to);
        if (fromDate == null) fromDate = LocalDate.now().withDayOfMonth(1);
        if (toDate == null) toDate = LocalDate.now();
        model.addAttribute("report", revenueReportService.getReport(fromDate, toDate, groupBy));
        return "admin/pages/revenuereport";
    }

    private LocalDate parseDateOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
