package com.viettridao.cafe.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.dto.request.table.SwitchTableRequest;
import com.viettridao.cafe.dto.response.table.TableResponse;
import com.viettridao.cafe.mapper.TableMapper;
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

                TableEntity fromTable = tableRepository.findById(fromTableId)
                                .orElseThrow(() -> new RuntimeException("Bàn không tồn tại: " + fromTableId));
                TableEntity toTable = tableRepository.findById(toTableId)
                                .orElseThrow(() -> new RuntimeException("Bàn không tồn tại: " + toTableId));

                if (toTable.getStatus() != TableStatus.AVAILABLE) {
                        throw new RuntimeException("Bàn chuyển đến phải đang trống!");
                }

                ReservationEntity fromReservation = fromTable.getReservations().stream()
                                .filter(r -> Boolean.FALSE.equals(r.getIsDeleted()))
                                .filter(r -> r.getInvoice() != null
                                                && r.getInvoice().getStatus() == InvoiceStatus.UNPAID)
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException(
                                                "Không tìm thấy đặt bàn chưa thanh toán cho bàn này!"));

                ReservationKey fromReservationId = fromReservation.getId();
                TableStatus fromTableStatusBeforeSwitch = fromTable.getStatus();

                // Tạo reservation mới trước, nếu thành công mới xóa reservation cũ
                ReservationEntity newReservation = new ReservationEntity();
                newReservation.setId(new ReservationKey(
                                toTableId,
                                fromReservationId.getIdEmployee(),
                                fromReservationId.getIdInvoice()));
                newReservation.setTable(toTable);
                newReservation.setEmployee(fromReservation.getEmployee());
                newReservation.setInvoice(fromReservation.getInvoice());
                newReservation.setCustomerName(fromReservation.getCustomerName());
                newReservation.setCustomerPhone(fromReservation.getCustomerPhone());
                newReservation.setReservationDate(fromReservation.getReservationDate());
                newReservation.setIsDeleted(false);
                reservationRepository.save(newReservation);

                // Xóa reservation cũ
                reservationRepository.delete(fromReservation);

                // Cập nhật trạng thái bàn
                fromTable.setStatus(TableStatus.AVAILABLE);
                toTable.setStatus(fromTableStatusBeforeSwitch);

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
