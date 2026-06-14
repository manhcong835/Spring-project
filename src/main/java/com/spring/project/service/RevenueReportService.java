package com.spring.project.service;

import com.spring.project.dto.RevenueReportView;

import java.time.LocalDate;

public interface RevenueReportService {

    /**
     * @param from    ngày bắt đầu (inclusive)
     * @param to      ngày kết thúc (inclusive)
     * @param groupBy "day" hoặc "month" — đơn vị nhóm trên timeline
     */
    RevenueReportView getReport(LocalDate from, LocalDate to, String groupBy);
}
