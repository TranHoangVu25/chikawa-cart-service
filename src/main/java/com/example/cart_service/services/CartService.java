package com.example.cart_service.services;

import com.example.cart_service.dto.request.CartItemRequest;
import com.example.cart_service.dto.response.ApiResponse;
import com.example.cart_service.models.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    void createCart(Integer userId);

    Cart findCartById(String id);

    List<Cart> findAllCart();

    Cart createCartItem(Integer userId, com.example.cart_service.models.CartItem newItem);

    ResponseEntity<ApiResponse<String>> deleteCartItem(Integer userId, String productionId, String variantId);

    Cart updateQuantity(Integer userId, CartItemRequest request);

    Cart findByUserId(Integer userId);

    Cart findByUserIdToCreate(Integer userId);

    ApiResponse<String> checkout(Integer userId,String jwtToken);

    ResponseEntity<ApiResponse<Cart>> addToCart(Integer userId, CartItemRequest request);
}
