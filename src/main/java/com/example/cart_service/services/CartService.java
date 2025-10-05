package com.example.cart_service.services;

import com.example.cart_service.dto.request.CartItemRequest;
import com.example.cart_service.models.Cart;
import com.example.cart_service.models.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    void createCart(Integer userId);

    Cart findCartById(String id);

    List<Cart> findAllCart();

    Cart createCartItem(Integer userId, CartItem newItem);

    Cart deleteCartItem(Integer userId, String productionId);

    Cart updateQuantity(Integer userId, CartItemRequest request);
}
