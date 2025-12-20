package com.example.cart_service.dto;

import com.example.cart_service.enums.Action;
import com.example.cart_service.models.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDeleteEvent {
    List<CartItem> orderItems;
    Action action;
    Integer userId;
}
