package com.viettridao.cafe.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.viettridao.cafe.dto.response.report.ReportResponse;
import com.viettridao.cafe.model.ImportEntity;
import com.viettridao.cafe.service.ImportService;
import com.viettridao.cafe.service.ReportService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ImportService importService;

    @GetMapping("/report")
    public String showReport(
            @RequestParam(value = "reportType", required = false, defaultValue = "all") String reportType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {

        if ("inventory".equals(reportType) && startDate != null && endDate != null) {
            // Lấy danh sách nhập hàng theo ngày
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<ImportEntity> importList = importService.getAllByImportDateRange(start, end);
            model.addAttribute("importList", importList);
            model.addAttribute("selectedType", "inventory");
        }
        // ...xử lý các loại report khác nếu cần...

        return "reports/report";
    }

}
