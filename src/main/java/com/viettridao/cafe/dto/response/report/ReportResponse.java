package com.viettridao.cafe.dto.response.report;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface ReportResponse {

    LocalDate getDateReport();

    Double getRevenue();

    Double getExpense();
}
