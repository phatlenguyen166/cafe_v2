package com.viettridao.cafe.dto.response.report;

import java.time.LocalDate;

public interface ReportResponse {

    LocalDate getDateReport();

    Double getRevenue();

    Double getExpense();
}
