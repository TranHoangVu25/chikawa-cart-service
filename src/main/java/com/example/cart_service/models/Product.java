package com.example.cart_service.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Document("products_embed")
public class Product {
    private String id;
    private String name;
    private Double price;
    private String status;
    private String images;
}
