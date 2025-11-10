package com.example.cart_service.controllers;

import com.example.cart_service.services.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SyncController {

    private final ProductServiceImpl syncService;

    /**
     * Gọi thủ công để đồng bộ toàn bộ sản phẩm từ Product Service vào Elasticsearch.
     */
    @PostMapping
    public ResponseEntity<String> syncAllProducts() {
        log.info("🔄 Yêu cầu đồng bộ dữ liệu sản phẩm nhận được...");
        try {
            syncService.syncAllProducts();
            return ResponseEntity.ok("✅ Đồng bộ dữ liệu sản phẩm thành công!");
        } catch (Exception e) {
            log.error("❌ Lỗi khi đồng bộ sản phẩm: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("❌ Đồng bộ thất bại: " + e.getMessage());
        }
    }
}

