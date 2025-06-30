package com.viettridao.cafe.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.mapping.Table;
import org.springframework.stereotype.Service;

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.dto.request.table.SwitchTableRequest;
import com.viettridao.cafe.dto.response.table.TableResponse;
import com.viettridao.cafe.mapper.TableMapper;
import com.viettridao.cafe.model.InvoiceDetailEntity;
import com.viettridao.cafe.model.InvoiceEntity;
import com.viettridao.cafe.model.InvoiceKey;
import com.viettridao.cafe.model.ReservationEntity;
import com.viettridao.cafe.model.ReservationKey;
import com.viettridao.cafe.model.TableEntity;
import com.viettridao.cafe.repository.InvoiceDetailRepository;
import com.viettridao.cafe.repository.InvoiceRepository;
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
        private final InvoiceRepository invoiceRepository;
        private final InvoiceDetailRepository invoiceDetailRepository;

        @Override
        @Transactional
        public void mergeTables(List<Integer> mergeTableIds, Integer targetTableId) {
                // Lấy bàn đích
                TableEntity targetTable = tableRepository.findById(targetTableId)
                                .orElseThrow(() -> new RuntimeException("Bàn gộp đến không tồn tại"));

                // Lấy danh sách bàn nguồn
                List<TableEntity> sourceTables = tableRepository.findAllById(mergeTableIds);

                // Lấy reservation & invoice của từng bàn nguồn
                Map<TableEntity, ReservationEntity> sourceReservations = new HashMap<>();
                Map<TableEntity, InvoiceEntity> sourceInvoices = new HashMap<>();
                for (TableEntity table : sourceTables) {
                        ReservationEntity reservation = table.getReservations().stream()
                                        .filter(r -> Boolean.FALSE.equals(r.getIsDeleted()))
                                        .filter(r -> r.getInvoice() != null
                                                        && r.getInvoice().getStatus() == InvoiceStatus.UNPAID)
                                        .findFirst()
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Không tìm thấy đặt bàn chưa thanh toán cho bàn "
                                                                        + table.getTableName()));
                        sourceReservations.put(table, reservation);
                        sourceInvoices.put(table, reservation.getInvoice());
                }

                boolean isTargetAvailable = targetTable.getStatus() == TableStatus.AVAILABLE;
                InvoiceEntity targetInvoice = new InvoiceEntity();
                ReservationEntity targetReservation = new ReservationEntity();

                if (isTargetAvailable) {
                        // Tạo hóa đơn mới cho bàn đích
                        targetInvoice = new InvoiceEntity();
                        targetInvoice.setStatus(InvoiceStatus.UNPAID);
                        targetInvoice.setCreatedAt(LocalDateTime.now());
                        targetInvoice.setIsDeleted(false);
                        targetInvoice.setInvoiceDetails(new ArrayList<>());

                        // targetInvoice.setTotalAmount();
                        invoiceRepository.save(targetInvoice);

                        // Tạo reservation mới cho bàn đích
                        ReservationEntity anySourceReservation = sourceReservations.values().iterator().next();
                        ReservationEntity newReservation = new ReservationEntity();
                        ReservationKey newKey = new ReservationKey(
                                        targetTableId,
                                        anySourceReservation.getEmployee().getId(),
                                        targetInvoice.getId());
                        newReservation.setId(newKey);
                        newReservation.setTable(targetTable);
                        newReservation.setEmployee(anySourceReservation.getEmployee());
                        newReservation.setInvoice(targetInvoice);
                        newReservation.setCustomerName(null);
                        newReservation.setCustomerPhone(null);
                        newReservation.setReservationDate(LocalDateTime.now());
                        newReservation.setIsDeleted(false);
                        reservationRepository.save(newReservation);
                        targetReservation = newReservation;

                        // Cập nhật trạng thái bàn đích
                        targetTable.setStatus(TableStatus.OCCUPIED);
                        tableRepository.save(targetTable);

                        // Gộp các chi tiết hóa đơn từ tất cả các hóa đơn nguồn, cộng dồn số lượng món
                        // trùng
                        Map<Integer, Integer> menuItemIdToQuantity = new HashMap<>();
                        Map<Integer, InvoiceDetailEntity> menuItemIdToDetail = new HashMap<>();

                        for (TableEntity source : sourceTables) {
                                InvoiceEntity sourceInvoice = sourceInvoices.get(source);
                                List<InvoiceDetailEntity> sourceDetails = sourceInvoice.getInvoiceDetails();
                                if (sourceDetails == null)
                                        sourceDetails = new ArrayList<>();
                                for (InvoiceDetailEntity detail : sourceDetails) {
                                        int menuItemId = detail.getMenuItem().getId();
                                        // Cộng dồn quantity
                                        menuItemIdToQuantity.put(menuItemId,
                                                        menuItemIdToQuantity.getOrDefault(menuItemId, 0)
                                                                        + detail.getQuantity());
                                        // Lưu lại 1 detail mẫu để lấy thông tin menu, price
                                        if (!menuItemIdToDetail.containsKey(menuItemId)) {
                                                InvoiceDetailEntity temp = new InvoiceDetailEntity();
                                                temp.setMenuItem(detail.getMenuItem());
                                                temp.setPrice(detail.getPrice());
                                                menuItemIdToDetail.put(menuItemId, temp);
                                        }
                                        invoiceDetailRepository.delete(detail);
                                }
                        }

                        // Sau khi đã cộng dồn, tạo mới các InvoiceDetailEntity cho hóa đơn mới
                        for (Map.Entry<Integer, Integer> entry : menuItemIdToQuantity.entrySet()) {
                                Integer menuItemId = entry.getKey();
                                Integer totalQuantity = entry.getValue();
                                InvoiceDetailEntity sample = menuItemIdToDetail.get(menuItemId);

                                InvoiceDetailEntity newDetail = new InvoiceDetailEntity();
                                newDetail.setId(new InvoiceKey(targetInvoice.getId(), menuItemId));
                                newDetail.setInvoice(targetInvoice);
                                newDetail.setMenuItem(sample.getMenuItem());
                                newDetail.setQuantity(totalQuantity);
                                newDetail.setPrice(sample.getPrice());
                                newDetail.setIsDeleted(false);
                                System.out.println("--------------------------");
                                System.out.println("menuItemId: " + menuItemId);
                                System.out.println("totalQuantity: " + totalQuantity);

                                invoiceDetailRepository.save(newDetail);

                        }
                } else {
                        // Bàn đích là một trong các bàn nguồn
                        targetReservation = sourceReservations.get(targetTable);
                        targetInvoice = sourceInvoices.get(targetTable);
                        if (targetInvoice == null)
                                throw new RuntimeException("Không tìm thấy hóa đơn bàn đích!");
                }

                // Gộp các hóa đơn nguồn vào hóa đơn đích
                for (TableEntity source : sourceTables) {
                        if (source.getId().equals(targetTableId))
                                continue; // bỏ qua bàn đích nếu là bàn nguồn

                        InvoiceEntity sourceInvoice = sourceInvoices.get(source);

                        List<InvoiceDetailEntity> sourceDetails = sourceInvoice.getInvoiceDetails();
                        if (sourceDetails == null)
                                sourceDetails = new ArrayList<>();

                        List<InvoiceDetailEntity> targetDetails = targetInvoice.getInvoiceDetails();
                        if (targetDetails == null)
                                targetDetails = new ArrayList<>();

                        // Cộng dồn hoặc chuyển các chi tiết hóa đơn sang hóa đơn đích
                        List<InvoiceDetailEntity> detailsToRemove = new ArrayList<>();
                        for (InvoiceDetailEntity detail : sourceDetails) {
                                // Kiểm tra trùng món
                                InvoiceDetailEntity existed = targetDetails.stream()
                                                .filter(d -> d.getMenuItem().getId()
                                                                .equals(detail.getMenuItem().getId()))
                                                .findFirst()
                                                .orElse(null);

                                if (existed != null) {
                                        existed.setQuantity(existed.getQuantity() + detail.getQuantity());
                                        invoiceDetailRepository.save(existed);
                                        detailsToRemove.add(detail); // sẽ xóa detail này sau
                                }

                        }
                        // Xóa các InvoiceDetail cũ đã cộng dồn
                        for (InvoiceDetailEntity detail : detailsToRemove) {
                                invoiceDetailRepository.delete(detail);
                        }
                        // --------------------
                        // Xóa reservation cũ
                        ReservationEntity sourceReservation = sourceReservations.get(source);
                        reservationRepository.delete(sourceReservation);

                        // Đổi trạng thái hóa đơn cũ thành MERGE_TABLE (bạn cần định nghĩa trạng thái
                        // này trong InvoiceStatus)
                        sourceInvoice.setStatus(InvoiceStatus.MERGE_TABLE);
                        sourceInvoice.setIsDeleted(false); // vẫn giữ lại hóa đơn
                        invoiceRepository.save(sourceInvoice);

                        // Cập nhật trạng thái bàn nguồn thành AVAILABLE
                        source.setStatus(TableStatus.AVAILABLE);
                        tableRepository.save(source);
                }
                // Tính tổng tiền từ các hóa đơn cũ (trừ hóa đơn đích nếu là bàn nguồn)
                double totalAmount = 0.0;
                for (TableEntity source : sourceTables) {
                        InvoiceEntity sourceInvoice = sourceInvoices.get(source);
                        // Nếu là bàn đích và không phải bàn trống thì bỏ qua (vì hóa đơn đích sẽ giữ
                        // lại)
                        if (!isTargetAvailable && source.getId().equals(targetTableId))
                                continue;
                        if (sourceInvoice != null && sourceInvoice.getTotalAmount() != null) {
                                totalAmount += sourceInvoice.getTotalAmount();
                        }
                }
                // Nếu bàn đích là một trong các bàn nguồn, cộng thêm tổng tiền hóa đơn đích cũ
                if (!isTargetAvailable && targetInvoice.getTotalAmount() != null) {
                        totalAmount += targetInvoice.getTotalAmount();
                }
                targetInvoice.setTotalAmount(totalAmount);
                invoiceRepository.save(targetInvoice);
        }

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

        @Override
        public TableEntity getTableById(Integer tableId) {
                return tableRepository.findById(tableId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn với ID: " + tableId));
        }

}
