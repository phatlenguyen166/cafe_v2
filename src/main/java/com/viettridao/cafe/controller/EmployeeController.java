package com.viettridao.cafe.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.viettridao.cafe.dto.request.employee.AddEmployeeRequest;
import com.viettridao.cafe.dto.response.employee.SearchEmployeeResponse;
import com.viettridao.cafe.model.EmployeeEntity;
import com.viettridao.cafe.model.PositionEntity;
import com.viettridao.cafe.service.EmployeeService;
import com.viettridao.cafe.service.PositionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeController extends BaseController {
    private final EmployeeService employeeService;
    private final PositionService positionService;

    @GetMapping("/employee")
    public String getEmployeePage(
            @RequestParam(value = "search", required = false) String searchKeyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // Tìm kiếm nhân viên theo từ khóa (không phân trang)
            List<?> employees = employeeService.searchByName(searchKeyword.trim());
            model.addAttribute("searchKeyword", searchKeyword);
            model.addAttribute("isSearchResult", true);
            model.addAttribute("listEmployee", employees);
        } else {
            // Lấy tất cả nhân viên với phân trang
            Pageable pageable = PageRequest.of(page, size);
            Page<SearchEmployeeResponse> employeePage = employeeService.getAllEmployees(pageable);

            model.addAttribute("isSearchResult", false);
            model.addAttribute("listEmployee", employeePage.getContent());
            model.addAttribute("employeePage", employeePage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", employeePage.getTotalPages());
            model.addAttribute("totalElements", employeePage.getTotalElements());
        }

        return "employees/employee";
    }

    @GetMapping("/employee/create")
    public String showCreateEmployee(Model model) {
        List<PositionEntity> positions = positionService.getAllPositions();

        model.addAttribute("listPosition", positions);
        return "employees/employee-create";
    }

    @PostMapping("/employee/create")
    public String createEmployee(@ModelAttribute AddEmployeeRequest request,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            RedirectAttributes redirectAttributes) {
        try {
            employeeService.createEmployee(request, avatar);
            redirectAttributes.addFlashAttribute("success", "Thêm nhân viên thành công!");
            return "redirect:/employee";

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();

            // Xử lý các loại lỗi cụ thể
            if (errorMessage.contains("số điện thoại")) {
                redirectAttributes.addFlashAttribute("phoneError", errorMessage);
            } else if (errorMessage.contains("username")) {
                redirectAttributes.addFlashAttribute("usernameError", errorMessage);
            } else {
                redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + errorMessage);
            }

            // Trả về dữ liệu đã nhập để không mất thông tin
            redirectAttributes.addFlashAttribute("employeeData", request);
            return "redirect:/employee/create";
        }
    }

    @GetMapping("/employee/edit/{id}")
    public String showEditEmployee(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            EmployeeEntity employee = employeeService.getEmployeeById(id);

            if (employee == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy nhân viên với ID: " + id);
                return "redirect:/employee";
            }

            model.addAttribute("employee", employee);
            return "employees/employee-edit";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải thông tin nhân viên");
            return "redirect:/employee";
        }
    }

}
