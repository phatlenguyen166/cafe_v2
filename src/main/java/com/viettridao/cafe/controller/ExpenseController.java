package com.viettridao.cafe.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.viettridao.cafe.dto.response.report.ReportResponse;
import com.viettridao.cafe.service.ReportService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ExpenseController {

    private final ReportService reportService;

    @GetMapping("/expense")
    public String showExpense(Model model) {
        model.addAttribute("totalRevenue", 0L);
        model.addAttribute("totalExpense", 0L);
        return "expenses/expense";
    }

    @GetMapping("/expense/by-date-range")
    public String getReportByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            Model model) {
        List<ReportResponse> reportList = reportService.getReportByDateRange(LocalDate.parse(startDate),
                LocalDate.parse(endDate));

        for (ReportResponse report : reportList) {
            System.out.println("-----------------------");
            System.out.println("Date: " + report.getDateReport());
            System.out.println("Total Revenue: " + report.getRevenue());
            System.out.println("Total Expense: " + report.getExpense());
        }
        model.addAttribute("reportList", reportList);

        return "expenses/expense";
    }

    @GetMapping("/expense/create")
    public String showCreateExpense() {
        return "expenses/expense-create";
    }
}
