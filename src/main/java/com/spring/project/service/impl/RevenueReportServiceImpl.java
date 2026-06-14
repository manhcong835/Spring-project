package com.spring.project.service.impl;

import com.spring.project.dto.RevenueByMethod;
import com.spring.project.dto.RevenueByTour;
import com.spring.project.dto.RevenuePoint;
import com.spring.project.dto.RevenueReportView;
import com.spring.project.entity.Payment;
import com.spring.project.repository.PaymentRepository;
import com.spring.project.service.RevenueReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@Service
public class RevenueReportServiceImpl implements RevenueReportService {

    private static final Locale VIETNAM_LOCALE = Locale.forLanguageTag("vi-VN");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DAY_KEY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_KEY = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final int TOP_TOUR_LIMIT = 10;
    private static final String GROUP_BY_MONTH = "month";
    private static final String GROUP_BY_WEEK = "week";

    private final PaymentRepository paymentRepository;

    public RevenueReportServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueReportView getReport(LocalDate from, LocalDate to, String groupBy) {
        if (from == null) from = LocalDate.now().withDayOfMonth(1);
        if (to == null) to = LocalDate.now();
        if (to.isBefore(from)) to = from;

        String safeGroupBy = switch (groupBy == null ? "" : groupBy.toLowerCase()) {
            case GROUP_BY_MONTH -> GROUP_BY_MONTH;
            case GROUP_BY_WEEK -> GROUP_BY_WEEK;
            default -> "day";
        };

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        List<Payment> payments = paymentRepository.findSuccessfulPaymentsBetween(start, end);

        BigDecimal total = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RevenueReportView(
                from.format(DATE_FORMATTER),
                to.format(DATE_FORMATTER),
                from.toString(),
                to.toString(),
                safeGroupBy,
                formatMoney(total),
                payments.size(),
                buildTimeline(payments, safeGroupBy),
                buildTopTours(payments),
                buildByMethod(payments, total)
        );
    }

    private List<RevenuePoint> buildTimeline(List<Payment> payments, String groupBy) {
        Map<String, BigDecimal> grouped = new TreeMap<>();
        for (Payment p : payments) {
            if (p.getPaidAt() == null) continue;
            String key = makeTimelineKey(p.getPaidAt(), groupBy);
            grouped.merge(key, p.getAmount(), BigDecimal::add);
        }

        BigDecimal max = grouped.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::max);

        List<RevenuePoint> points = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : grouped.entrySet()) {
            BigDecimal amount = entry.getValue();
            points.add(new RevenuePoint(
                    timelineLabel(entry.getKey(), groupBy),
                    formatMoney(amount),
                    amount.longValue(),
                    calculateBarHeight(amount, max)
            ));
        }
        return points;
    }

    private String makeTimelineKey(LocalDateTime dt, String groupBy) {
        return switch (groupBy) {
            case GROUP_BY_MONTH -> dt.format(MONTH_KEY);
            case GROUP_BY_WEEK -> String.format("%d-W%02d",
                    dt.get(WeekFields.ISO.weekBasedYear()),
                    dt.get(WeekFields.ISO.weekOfWeekBasedYear()));
            default -> dt.format(DAY_KEY);
        };
    }

    private String timelineLabel(String key, String groupBy) {
        return switch (groupBy) {
            // "2026-06" -> "06/2026"
            case GROUP_BY_MONTH -> key.substring(5) + "/" + key.substring(0, 4);
            // "2026-W23" -> "T23/2026"
            case GROUP_BY_WEEK -> "T" + key.substring(6) + "/" + key.substring(0, 4);
            // "2026-06-14" -> "14/06"
            default -> key.substring(8) + "/" + key.substring(5, 7);
        };
    }

    private List<RevenueByTour> buildTopTours(List<Payment> payments) {
        Map<String, BigDecimal> sumByTour = new LinkedHashMap<>();
        Map<String, Long> countByTour = new LinkedHashMap<>();

        for (Payment p : payments) {
            String tourName = p.getBooking().getTourDeparture().getTour().getName();
            sumByTour.merge(tourName, p.getAmount(), BigDecimal::add);
            countByTour.merge(tourName, 1L, Long::sum);
        }

        return sumByTour.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(TOP_TOUR_LIMIT)
                .map(entry -> new RevenueByTour(
                        entry.getKey(),
                        countByTour.getOrDefault(entry.getKey(), 0L),
                        formatMoney(entry.getValue())
                ))
                .toList();
    }

    private List<RevenueByMethod> buildByMethod(List<Payment> payments, BigDecimal total) {
        Map<String, BigDecimal> sumByMethod = new LinkedHashMap<>();
        Map<String, Long> countByMethod = new LinkedHashMap<>();

        for (Payment p : payments) {
            String method = p.getPaymentMethod() == null ? "OTHER" : p.getPaymentMethod();
            sumByMethod.merge(method, p.getAmount(), BigDecimal::add);
            countByMethod.merge(method, 1L, Long::sum);
        }

        List<RevenueByMethod> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : sumByMethod.entrySet()) {
            BigDecimal amount = entry.getValue();
            int percent = (total == null || total.compareTo(BigDecimal.ZERO) <= 0)
                    ? 0
                    : amount.multiply(BigDecimal.valueOf(100))
                            .divide(total, 0, RoundingMode.HALF_UP)
                            .intValue();
            result.add(new RevenueByMethod(
                    entry.getKey(),
                    methodLabel(entry.getKey()),
                    countByMethod.getOrDefault(entry.getKey(), 0L),
                    formatMoney(amount),
                    percent
            ));
        }
        result.sort(Comparator.comparingInt(RevenueByMethod::getPercentOfTotal).reversed());
        return result;
    }

    private int calculateBarHeight(BigDecimal amount, BigDecimal max) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || max.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        int percent = amount.multiply(BigDecimal.valueOf(100))
                .divide(max, 0, RoundingMode.HALF_UP)
                .intValue();
        return Math.max(percent, 8);
    }

    private String formatMoney(BigDecimal value) {
        BigDecimal safe = value == null ? BigDecimal.ZERO : value;
        return NumberFormat.getIntegerInstance(VIETNAM_LOCALE).format(safe) + " VND";
    }

    private String methodLabel(String code) {
        return switch (code) {
            case "CASH" -> "Tien mat";
            case "BANK_TRANSFER" -> "Chuyen khoan";
            case "MOMO" -> "MoMo";
            case "VNPAY" -> "VNPay";
            case "ZALOPAY" -> "ZaloPay";
            default -> code;
        };
    }
}
