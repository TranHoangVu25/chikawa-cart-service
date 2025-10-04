package com.example.cart_service.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CartItem {
    String product_id;
    String product_name;
    double price;
    int quantity;
    String img_url;
}
