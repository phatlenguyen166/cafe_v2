package com.viettridao.cafe.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.dto.request.table.SwitchTableRequest;
import com.viettridao.cafe.dto.response.table.TableResponse;
import com.viettridao.cafe.mapper.TableMapper;
import com.viettridao.cafe.model.InvoiceEntity;
import com.viettridao.cafe.model.ReservationEntity;
import com.viettridao.cafe.model.ReservationKey;
import com.viettridao.cafe.model.TableEntity;
import com.viettridao.cafe.repository.ReservationRepository;
import com.viettridao.cafe.repository.TableRepository;
import com.viettridao.cafe.service.TableSerivce;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TableSerivceImpl implements TableSerivce {

    private final TableRepository tableRepository;
    private final TableMapper tableMapper;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public TableResponse switchTable(SwitchTableRequest request) {
        Integer fromTableId = request.getFromTableId();
        Integer toTableId = request.getToTableId();

        if (fromTableId.equals(toTableId)) {
            throw new RuntimeException("Không thể chuyển sang chính bàn này!");
        }

        // Lấy bàn cũ và mới
        TableEntity fromTable = tableRepository.findById(fromTableId)
                .orElseThrow(() -> new RuntimeException("Bàn không tồn tại: " + fromTableId));
        TableEntity toTable = tableRepository.findById(toTableId)
                .orElseThrow(() -> new RuntimeException("Bàn không tồn tại: " + toTableId));

        if (toTable.getStatus() != TableStatus.AVAILABLE) {
            throw new RuntimeException("Bàn chuyển đến phải đang trống!");
        }

        // Lấy đặt bàn chưa thanh toán
        ReservationEntity oldReservation = fromTable.getReservations().stream()
                .filter(reservation -> Boolean.FALSE.equals(reservation.getIsDeleted()))
                .filter(reservation -> reservation.getInvoice() != null &&
                        reservation.getInvoice().getStatus() == InvoiceStatus.UNPAID)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn chưa thanh toán cho bàn này!"));
        reservationRepository.delete(oldReservation);
        ReservationKey oldId = oldReservation.getId();

        // Lưu trạng thái cũ của bàn fromTable
        TableStatus fromTableOldStatus = fromTable.getStatus();

        // Tạo bản mới
        ReservationEntity newReservation = new ReservationEntity();
        newReservation.setId(new ReservationKey(
                toTableId,
                oldId.getIdEmployee(),
                oldId.getIdInvoice()));
        newReservation.setTable(toTable);
        newReservation.setEmployee(oldReservation.getEmployee());
        newReservation.setInvoice(oldReservation.getInvoice());
        newReservation.setCustomerName(oldReservation.getCustomerName());
        newReservation.setCustomerPhone(oldReservation.getCustomerPhone());
        newReservation.setReservationDate(oldReservation.getReservationDate());
        newReservation.setIsDeleted(false);
        reservationRepository.save(newReservation);

        // Cập nhật trạng thái bàn
        fromTable.setStatus(TableStatus.AVAILABLE);
        toTable.setStatus(fromTableOldStatus);

        tableRepository.save(fromTable);
        TableEntity updatedToTable = tableRepository.save(toTable);

        return tableMapper.convertToDto(updatedToTable);

    }

    @Override
    public List<TableResponse> getTableByStatus(TableStatus status) {
        return tableRepository.findByStatus(status).stream()
                .map(tableMapper::convertToDto)
                .toList();
    }

    @Override
    public List<TableResponse> getAllTables() {
        return tableRepository.findAllByIsDeletedFalse()
                .stream()
                .map(tableMapper::convertToDto)
                .toList();
    }

}
