package com.viettridao.cafe.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.viettridao.cafe.dto.response.report.ReportResponse;
import com.viettridao.cafe.repository.ReportRepository;
import com.viettridao.cafe.service.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public List<ReportResponse> getReportByDateRange(LocalDate startDate, LocalDate endDate) {
        return reportRepository.getRevenueExpenseBetween(startDate, endDate);
    }

}
