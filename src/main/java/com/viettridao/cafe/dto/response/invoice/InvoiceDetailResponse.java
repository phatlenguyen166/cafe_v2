package com.viettridao.cafe.dto.response.invoice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceDetailResponse {
    private Integer menuItemId;
    private Integer quantity;
    private Double price;
    private Boolean isDeleted;
}
