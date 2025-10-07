package com.example.cart_service.dto.request;

import com.example.cart_service.models.CartItem;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequestDTO {
    Integer userId;
    List<CartItem> items;
    // getter/setter
}
