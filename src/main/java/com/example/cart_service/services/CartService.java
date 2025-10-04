package com.example.cart_service.services;

import com.example.cart_service.models.Cart;
import org.springframework.stereotype.Service;
@Service
public interface CartService {
    void createCart(Integer userId);

    Cart findCartById(String id);
}
