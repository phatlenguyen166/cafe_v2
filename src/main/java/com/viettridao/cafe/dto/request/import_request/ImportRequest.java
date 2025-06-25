package com.viettridao.cafe.dto.request.import_request;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportRequest {
    @NotNull(message = "Ngày nhập không được để trống")
    private LocalDate importDate;

    @NotNull(message = "Tổng tiền không được để trống")
    @Min(value = 0, message = "Tổng tiền phải lớn hơn hoặc bằng 0")
    private Double totalAmount;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    private Boolean isDeleted = false;

    @NotNull(message = "Nhân viên không được để trống")
    private Integer employeeId;

    private Integer productId;
}
