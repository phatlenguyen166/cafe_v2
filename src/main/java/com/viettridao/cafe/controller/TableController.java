package com.viettridao.cafe.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

import com.viettridao.cafe.dto.request.reservation.ReservationRequest;
import com.viettridao.cafe.dto.response.table.TableResponse;
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

    @GetMapping("/sale")
    public String showTable(Model model) {
        try {
            List<TableResponse> listTables = tableService.getAllTables();
            model.addAttribute("listTables", listTables);
            return "tables/table";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Không thể tải danh sách bàn.");
            return "error"; // hoặc trả về trang lỗi riêng của bạn
        }
    }

    @PostMapping("/sale/create-reservation")
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

}