package com.viettridao.cafe.dto.response.equipment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentResponse {
    private Integer id;
    private String equipmentName;
    private Integer quantity;
    private Double purchasePrice;
    private Boolean isDeleted;
}
