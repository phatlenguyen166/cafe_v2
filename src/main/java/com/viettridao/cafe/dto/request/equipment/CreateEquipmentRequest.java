package com.viettridao.cafe.dto.request.equipment;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEquipmentRequest {

    @NotBlank(message = "Tên thiết bị không được để trống")
    @Size(min = 2, max = 100, message = "Tên thiết bị phải từ 2 đến 100 ký tự")
    private String equipmentName;

    @NotNull(message = "Giá mua không được để trống")
    @DecimalMin(value = "1000.0", message = "Giá mua phải ít nhất 1.000 VNĐ")
    @DecimalMax(value = "1000000000.0", message = "Giá mua không được quá 1 tỷ VNĐ")
    private Double purchasePrice;

    // Mặc định quantity = 0, không cần validation
    private Integer quantity = 0;

    private Boolean isDeleted = false;
}
