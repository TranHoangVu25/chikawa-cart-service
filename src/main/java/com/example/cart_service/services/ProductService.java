package com.example.cart_service.services;

import com.example.cart_service.dto.response.ApiResponse;
import com.example.cart_service.models.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    ApiResponse<List<Product>> getAllProduct();

    void syncAllProducts();
}
