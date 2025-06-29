package com.viettridao.cafe.service;

import com.viettridao.cafe.model.InvoiceEntity;

public interface InvoiceService {
    InvoiceEntity getByTableId(Integer TableId);
}
