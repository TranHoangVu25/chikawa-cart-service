package com.example.cart_service.services;

import com.example.cart_service.dto.response.ApiResponse;
import com.example.cart_service.models.Product;
import com.example.cart_service.repositories.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    RestTemplate restTemplate;

    @Value("${product.service.url}")
    String productServiceUrl;

    @Override
    public ApiResponse<List<Product>> getAllProduct() {
        return ApiResponse.<List<Product>>builder()
                .result(productRepository.findAll())
                .build();
    }

    public void syncAllProducts() {
        ResponseEntity<ApiResponse<Product[]>> response = restTemplate.exchange(
                productServiceUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<Product[]>>() {
                }
        );

        Product[] products = response.getBody().getResult();
        if (products == null || products.length == 0) {
            System.out.println("⚠️ Không có sản phẩm nào để đồng bộ.");
            return;
        }

        List<Product> docs = Arrays.stream(products)
                .map(p -> Product.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .price(p.getPrice())
                        .status(p.getStatus())
                        .images(
                                (p.getImages() != null && !p.getImages().isEmpty())
                                        ? List.of(p.getImages().get(0))
                                        : List.of()
                        )
                        .build())
                .toList();

        productRepository.saveAll(docs);
        System.out.println(docs);
        System.out.println("✅ Đã đồng bộ " + docs.size() + " sản phẩm vào product embedded.");
    }
}
