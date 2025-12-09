package com.example.cart_service.controllers;

import com.example.cart_service.dto.ProductDTO;
import com.example.cart_service.dto.response.ApiResponse;
import com.example.cart_service.models.Product;
import com.example.cart_service.services.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/pro_embedded")
@Slf4j
public class ProductController {
    ProductService productService;

    @GetMapping("")
    public ApiResponse<List<Product>> getAllProduct(){
        return productService.getAllProduct();
    }

    @PostMapping("")
    public ResponseEntity<String> syncAllProducts() {
        log.info("Yêu cầu đồng bộ dữ liệu sản phẩm nhận được...");
        try {
            productService.syncAllProducts();
            return ResponseEntity.ok("Đồng bộ dữ liệu sản phẩm thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi đồng bộ sản phẩm: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Đồng bộ thất bại: " + e.getMessage());
        }
    }
}
