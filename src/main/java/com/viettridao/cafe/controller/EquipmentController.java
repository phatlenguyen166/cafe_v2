package com.viettridao.cafe.controller;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.viettridao.cafe.dto.request.equipment.CreateEquipmentRequest;
import com.viettridao.cafe.dto.request.import_request.ImportRequest;
import com.viettridao.cafe.dto.response.equipment.EquipmentResponse;
import com.viettridao.cafe.service.EquipmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EquipmentController extends BaseController {

    private final EquipmentService equipmentService;

    @GetMapping("/device")
    public String showDevice(Model model) {
        List<EquipmentResponse> listEquipment = equipmentService.getAllEquipments();
        model.addAttribute("listEquipment", listEquipment);
        return "devices/device";
    }

    @GetMapping("/device/create")
    public String showCreateDevice(Model model) {
        model.addAttribute("createEquipmentRequest", new CreateEquipmentRequest());
        return "devices/device-create";
    }

    @PostMapping("/device/create")
    public String createDevice(@Valid @ModelAttribute CreateEquipmentRequest createEquipmentRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Kiểm tra validation errors
            if (bindingResult.hasErrors()) {
                log.warn("Validation failed for equipment creation: {}", bindingResult.getAllErrors());
                model.addAttribute("createEquipmentRequest", createEquipmentRequest);
                model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin nhập vào!");
                return "devices/device-create";
            }

            // Gọi service để tạo thiết bị
            equipmentService.createEquipment(createEquipmentRequest);

            log.info("Equipment created successfully: {}", createEquipmentRequest.getEquipmentName());

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thêm thiết bị '" + createEquipmentRequest.getEquipmentName() + "' thành công!");

            return "redirect:/device";

        } catch (IllegalArgumentException e) {
            log.error("Invalid argument when creating equipment: {}", e.getMessage());
            model.addAttribute("createEquipmentRequest", createEquipmentRequest);
            model.addAttribute("errorMessage", "Dữ liệu không hợp lệ: " + e.getMessage());
            return "devices/device-create";

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation when creating equipment: {}", e.getMessage());
            model.addAttribute("createEquipmentRequest", createEquipmentRequest);
            model.addAttribute("errorMessage", "Thiết bị đã tồn tại hoặc vi phạm ràng buộc dữ liệu!");
            return "devices/device-create";

        } catch (DataAccessException e) {
            log.error("Database access error when creating equipment: {}", e.getMessage());
            model.addAttribute("createEquipmentRequest", createEquipmentRequest);
            model.addAttribute("errorMessage", "Lỗi kết nối cơ sở dữ liệu. Vui lòng thử lại!");
            return "devices/device-create";

        } catch (Exception e) {
            log.error("Unexpected error when creating equipment: ", e);
            model.addAttribute("createEquipmentRequest", createEquipmentRequest);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi thêm thiết bị. Vui lòng thử lại!");
            return "devices/device-create";
        }
    }
    // @GetMapping("/device/import")
    // public String showImportDevice(Model model) {
    // List<EquipmentResponse> listEquipment = equipmentService.getAllEquipments();
    // model.addAttribute("listEquipment", listEquipment);
    // model.addAttribute("importRequest", new ImportRequest());
    // return "devices/device-import";
    // }

    // @PostMapping("/device/import")
    // public String showImportDevice(ImportRequest request, Model model) {

    // return "devices/device-import";
    // }
}
