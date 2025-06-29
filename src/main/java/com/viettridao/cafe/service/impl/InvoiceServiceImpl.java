package com.viettridao.cafe.service.impl;

import org.springframework.stereotype.Service;

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.model.InvoiceEntity;
import com.viettridao.cafe.model.ReservationEntity;
import com.viettridao.cafe.model.TableEntity;
import com.viettridao.cafe.repository.TableRepository;
import com.viettridao.cafe.service.InvoiceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final TableRepository tableRepository;

    @Override
    public InvoiceEntity getByTableId(Integer TableId) {
        TableEntity table = tableRepository.findById(TableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));
        return table.getReservations().stream()
                .filter(reservation -> Boolean.FALSE.equals(reservation.getIsDeleted()))
                .filter(reservation -> reservation.getInvoice() != null
                        && reservation.getInvoice().getStatus() == InvoiceStatus.UNPAID)
                .map(ReservationEntity::getInvoice)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn chưa thanh toán cho bàn này!"));
    }

}
