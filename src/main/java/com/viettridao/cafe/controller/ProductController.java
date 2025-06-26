package com.viettridao.cafe.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.viettridao.cafe.dto.response.product.ProductResponse;
import com.viettridao.cafe.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product")
    public String showProduct(Model model) {

        List<ProductResponse> products = productService.getAllProducts();

        model.addAttribute("listProduct", products);
        return "products/product";
    }

    @GetMapping("/product/import")
    public String showImportProduct() {
        return "products/product-import";
    }

    @GetMapping("/product/export")
    public String showExportProduct() {
        return "products/product-export";
    }
}
