package com.viettridao.cafe.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

import com.viettridao.cafe.dto.request.reservation.ReservationRequest;
import com.viettridao.cafe.dto.request.table.SwitchTableRequest;
import com.viettridao.cafe.dto.request.invoice.InvoiceRequest;
import com.viettridao.cafe.dto.response.menu_item.MenuItemResponse;
import com.viettridao.cafe.dto.response.table.TableResponse;
import com.viettridao.cafe.model.InvoiceDetailEntity;
import com.viettridao.cafe.model.InvoiceEntity;
import com.viettridao.cafe.model.ReservationEntity;
import com.viettridao.cafe.model.TableEntity;
import com.viettridao.cafe.service.InvoiceDetailService;
import com.viettridao.cafe.service.InvoiceService;
import com.viettridao.cafe.service.MenuItemService;
import com.viettridao.cafe.service.ReservationService;
import com.viettridao.cafe.service.TableSerivce;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TableController extends BaseController {

    private final TableSerivce tableService;
    private final ReservationService reservationService;
    private final MenuItemService menuItemService;
    private final InvoiceDetailService invoiceDetailService;
    private final InvoiceService invoiceService;

    @GetMapping("/sale")
    public String showTable(Model model) {
        try {
            List<TableResponse> listTables = tableService.getAllTables();
            List<MenuItemResponse> listMenuItems = menuItemService.getAllMenuItems();
            model.addAttribute("listMenuItems", listMenuItems);
            model.addAttribute("listTables", listTables);
            return "tables/table";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Không thể tải danh sách bàn.");
            return "error";
        }
    }

    @GetMapping("/sale/view-table")
    public String showMenu(@ModelAttribute("tableId") Integer tableId, Model model, HttpSession session) {
        try {
            // Lấy danh sách bàn và menu để render lại giao diện
            List<TableResponse> listTables = tableService.getAllTables();
            List<MenuItemResponse> listMenuItems = menuItemService.getAllMenuItems();
            model.addAttribute("listTables", listTables);
            model.addAttribute("listMenuItems", listMenuItems);

            // Lấy hóa đơn chưa thanh toán của bàn
            InvoiceEntity invoice = invoiceService.getByTableId(tableId);

            // Lấy chi tiết hóa đơn (các món đã gọi)
            List<InvoiceDetailEntity> invoiceDetails = invoice.getInvoiceDetails();
            model.addAttribute("invoiceDetails", invoiceDetails);

            // Lấy thông tin reservation (nếu cần)
            ReservationEntity reservation = invoice.getReservations().stream()
                    .filter(r -> Boolean.FALSE.equals(r.getIsDeleted()))
                    .findFirst()
                    .orElse(null);
            model.addAttribute("reservation", reservation);
            model.addAttribute("listMenuDetails", invoiceDetails);
            // Truyền tableId để biết đang xem bàn nào
            model.addAttribute("selectedTableId", tableId);

            return "tables/table";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            // Render lại danh sách bàn để tránh lỗi giao diện
            model.addAttribute("listTables", tableService.getAllTables());
            model.addAttribute("listMenuItems", menuItemService.getAllMenuItems());
            return "tables/table";
        }
    }

    @PostMapping("/sale/create-menu")
    public String createMenu(
            @ModelAttribute InvoiceRequest invoiceRequest,
            RedirectAttributes redirectAttributes, HttpSession session) {
        try {

            invoiceDetailService.createInvoiceDetail(invoiceRequest, getCurrentUser(session).getEmployee().getId());
            redirectAttributes.addFlashAttribute("successMessage", "Thêm thực đơn thành công!");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi thêm thực đơn.");
        }
        return "redirect:/sale";
    }

    @PostMapping("/sale/switch-table")
    public String switchTable(
            @Valid @ModelAttribute SwitchTableRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()
                || request.getFromTableId() == null
                || request.getToTableId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn đầy đủ thông tin chuyển bàn.");
            return "redirect:/sale";
        }
        if (request.getFromTableId().equals(request.getToTableId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể chuyển sang chính bàn này!");
            return "redirect:/sale";
        }
        try {
            tableService.switchTable(request);
            redirectAttributes.addFlashAttribute("successMessage", "Chuyển bàn thành công!");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không xác định khi chuyển bàn.");
        }
        return "redirect:/sale";
    }

    @PostMapping("/sale/cancel-table")
    public String cancelReservation(
            @ModelAttribute("tableId") Integer tableId,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            reservationService.cancelReservation(tableId);
            redirectAttributes.addFlashAttribute("successMessage", "Hủy đặt bàn thành công!");
            return "redirect:/sale";
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/sale";
        }
    }

    @PostMapping("/sale/create-table")
    public String createReservation(
            @Valid @ModelAttribute ReservationRequest request,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) { // Thêm RedirectAttributes

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Dữ liệu đặt bàn không hợp lệ!");
            model.addAttribute("org.springframework.validation.BindingResult.reservationRequest", bindingResult);
            model.addAttribute("reservationRequest", request);
            model.addAttribute("listTables", tableService.getAllTables());
            return "tables/table";
        }

        try {
            reservationService.createReservation(request, getCurrentUser(session).getEmployee().getId());
            // Truyền thông báo qua redirect
            redirectAttributes.addFlashAttribute("successMessage", "Đặt bàn thành công!");
            return "redirect:/sale";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("listTables", tableService.getAllTables());
            return "tables/table";
        }
    }

    // Render dữ liệu hóa đơn vào modal thanh toán
    @GetMapping("/sale/payment")
    public String showPaymentModal(@RequestParam("tableId") Integer tableId, Model model) {

        TableEntity table = tableService.getTableById(tableId);
        InvoiceEntity invoice = invoiceService.getByTableId(tableId);
        List<InvoiceDetailEntity> invoiceDetails = invoice.getInvoiceDetails();
        for (InvoiceDetailEntity invoiceDetailEntity : invoiceDetails) {
            System.out.println("-------Invoice Details: " + invoiceDetailEntity);
        }
        System.out.println("---------Total Amount: " + invoice.getTotalAmount());
        model.addAttribute("invoiceDetails", invoiceDetails);
        model.addAttribute("paymentTotal", invoice.getTotalAmount());
        model.addAttribute("paymentTableName", table.getTableName());
        model.addAttribute("selectedTableId", tableId);
        model.addAttribute("listTables", tableService.getAllTables());

        return "tables/table";
    }

    // Xử lý thanh toán (POST)
    @PostMapping("/sale/payment")
    public String processPayment(@RequestParam("tableId") Integer tableId, RedirectAttributes redirectAttributes) {
        try {
            invoiceService.checkout(tableId);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán thất bại: " + e.getMessage());
        }
        return "redirect:/sale";
    }
}