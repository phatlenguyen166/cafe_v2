package com.viettridao.cafe.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.viettridao.cafe.dto.response.report.ReportResponse;
import com.viettridao.cafe.service.ReportService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ExpenseController {

    private final ReportService reportService;

    @GetMapping("/expense")
    public String showExpense() {

        LocalDate startDate = LocalDate.parse("2025-06-25");
        LocalDate endDate = LocalDate.parse("2025-06-28");

        List<ReportResponse> reportResponses = reportService.getReportByDateRange(startDate, endDate);

        for (ReportResponse reportResponse : reportResponses) {
            System.out.println("Report----------: " + reportResponse.getDateReport());
            System.out.println("Total Revenue: " + reportResponse.getRevenue());
            System.out.println("Total Expense: " + reportResponse.getExpense());
        }
        return "expenses/expense";
    }

    @GetMapping("/expense/create")
    public String showCreateExpense() {
        return "expenses/expense-create";
    }
}
