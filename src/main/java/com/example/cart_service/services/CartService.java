package com.example.cart_service.services;

import com.example.cart_service.models.Cart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    void createCart(Integer userId);

    Cart findCartById(String id);

    List<Cart> findAllCart();
}
