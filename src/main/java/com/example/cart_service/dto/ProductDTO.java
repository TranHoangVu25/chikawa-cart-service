package com.example.cart_service.dto;

import com.example.cart_service.enums.Action;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO {
    String id;
    String name;
    double price;
    String status;
    List<String> images;
    Action action;
}
