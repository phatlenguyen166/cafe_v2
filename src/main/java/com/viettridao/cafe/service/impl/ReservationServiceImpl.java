package com.viettridao.cafe.service.impl;

import org.springframework.stereotype.Service;

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.dto.request.reservation.ReservationRequest;
import com.viettridao.cafe.dto.response.reservation.ReservationResponse;
import com.viettridao.cafe.mapper.ReservationMapper;
import com.viettridao.cafe.model.EmployeeEntity;
import com.viettridao.cafe.model.InvoiceEntity;
import com.viettridao.cafe.model.ReservationEntity;
import com.viettridao.cafe.model.ReservationKey;
import com.viettridao.cafe.model.TableEntity;
import com.viettridao.cafe.repository.EmployeeRepository;
import com.viettridao.cafe.repository.InvoiceRepository;
import com.viettridao.cafe.repository.ReservationRepository;
import com.viettridao.cafe.repository.TableRepository;
import com.viettridao.cafe.service.ReservationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final EmployeeRepository employeeRepository;
    private final InvoiceRepository invoiceRepository;
    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, Integer employeeId) {
        try {
            EmployeeEntity employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            TableEntity table = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new RuntimeException("Table not found"));

            // Tạo hóa đơn rỗng
            InvoiceEntity invoice = new InvoiceEntity();
            invoice.setTotalAmount(null);
            invoice.setCreatedAt(request.getReservationDate());
            invoice.setStatus(InvoiceStatus.UNPAID);
            invoice.setIsDeleted(false);
            invoice.setPromotion(null);
            invoice = invoiceRepository.save(invoice);

            // Tạo chi tiết đặt bàn
            ReservationEntity reservation = new ReservationEntity();
            reservation.setId(new ReservationKey(request.getTableId(), employeeId, invoice.getId()));
            reservation.setTable(table);
            reservation.setEmployee(employee);
            reservation.setInvoice(invoice);
            reservation.setCustomerName(request.getCustomerName());
            reservation.setCustomerPhone(request.getCustomerPhone());
            reservation.setReservationDate(request.getReservationDate());
            reservation.setIsDeleted(false);
            reservation = reservationRepository.save(reservation);

            // Cập nhật trạng thái bàn
            table.setStatus(TableStatus.RESERVED);
            tableRepository.save(table);

            return reservationMapper.convertToResponse(reservation);
        } catch (RuntimeException ex) {
            throw new RuntimeException("Tạo đặt bàn thất bại: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Đã xảy ra lỗi không xác định khi tạo đặt bàn.", ex);
        }
    }

}
