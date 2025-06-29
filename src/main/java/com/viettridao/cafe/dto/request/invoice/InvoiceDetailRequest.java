package com.viettridao.cafe.dto.request.invoice;

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
public class InvoiceDetailRequest {
    private Integer menuItemId;
    private Integer quantity;
    private Double price;

}
