package com.example.cart_service.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Product {
    private String id;
    private String name;
    private Double price;
    private String status;
    private String images;
}
