package com.example.cart_service.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CartItem {
    @Id
    String product_id;
    String product_name;
    double price;
    int quantity;
    String img_url;
}
