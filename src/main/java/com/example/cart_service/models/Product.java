package com.example.cart_service.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Document("products_embed") // map với product_service
public class Product {
    private String id;
    private String name;
    private Double price;
    private String status;
    private List<String> images;
}
