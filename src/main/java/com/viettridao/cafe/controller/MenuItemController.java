package com.viettridao.cafe.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.viettridao.cafe.dto.request.menu_item.CreateMenuDetailRequest;
import com.viettridao.cafe.dto.request.menu_item.CreateMenuItemRequest;
import com.viettridao.cafe.dto.response.menu_item.MenuItemResponse;
import com.viettridao.cafe.dto.response.product.ProductResponse;
import com.viettridao.cafe.mapper.ProductMapper;
import com.viettridao.cafe.repository.ProductRepository;
import com.viettridao.cafe.service.MenuItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;

@Controller
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @GetMapping("/menu")
    public String showMenu(Model model) {

        List<MenuItemResponse> listMenuItems = menuItemService.getAllMenuItems();

        model.addAttribute("listMenuItems", listMenuItems);

        return "menus/menu";
    }

    @GetMapping("/menu/create")
    public String showCreateMenu(Model model) {

        List<ProductResponse> listProducts = productRepository.findAll().stream()
                .map(productMapper::convertToDto)
                .toList();
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("createMenuItemRequest", new CreateMenuItemRequest());
        return "menus/menu-create";
    }

    @PostMapping("/menu/create")
    public String createMenu(
            @Valid CreateMenuItemRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // Lấy lại danh sách sản phẩm để render lại form
            List<ProductResponse> listProducts = productRepository.findAll().stream()
                    .map(productMapper::convertToDto)
                    .toList();
            model.addAttribute("listProducts", listProducts);
            model.addAttribute("menuItem", request);
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin!");
            return "menus/menu-create";
        }

        // System.out.println("Tên món-------------------: " + request.getItemName());
        // System.out.println("Giá món-------------------: " +
        // request.getCurrentPrice());
        // for (CreateMenuDetailRequest item : request.getMenuDetails()) {
        // System.out.println("Sản phẩm ID----------------: " + item.getProductId());
        // System.out.println("Số lượng-------------------: " + item.getQuantity());
        // System.out.println("Giá------------------------: " + item.getUnitName());
        // }
        try {
            menuItemService.createMenu(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm món thành công!");
            return "redirect:/menu";
        } catch (Exception e) {
            e.printStackTrace(); // Thêm dòng này để log lỗi chi tiết ra console
            model.addAttribute("errorMessage", "Có lỗi xảy ra, vui lòng thử lại!");
            List<ProductResponse> listProducts = productRepository.findAll().stream()
                    .map(productMapper::convertToDto)
                    .toList();
            model.addAttribute("listProducts", listProducts);
            model.addAttribute("menuItem", request);
            return "menus/menu-create";
        }
    }

    @GetMapping("/menu/edit")
    public String showEditMenu() {
        return "menus/menu-edit";
    }

    @GetMapping("/menu/delete/{id}")
    public String deleteMenu(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            menuItemService.deleteMenuItem(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa món thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xóa món thất bại!");
        }
        return "redirect:/menu";
    }
}
