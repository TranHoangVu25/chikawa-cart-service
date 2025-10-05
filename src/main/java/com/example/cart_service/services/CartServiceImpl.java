package com.example.cart_service.services;

import com.example.cart_service.models.Cart;
import com.example.cart_service.models.CartItem;
import com.example.cart_service.repositories.CartRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CartServiceImpl implements CartService{
    CartRepository cartRepository;
    @Override
    public void createCart(Integer userId) {
        List<CartItem> cartItemList = new ArrayList<>();
        Cart cart = new Cart()
                .builder()
                .user_id(userId)
                .cart_items(cartItemList)
                .build();
        cartRepository.save(cart);
    }

    @Override
    public Cart findCartById(String id) {
        return cartRepository.findById(id)
                .orElseThrow(()->new RuntimeException("id not found"));
    }

    @Override
    public List<Cart> findAllCart() {
        return cartRepository.findAll();
    }
}
