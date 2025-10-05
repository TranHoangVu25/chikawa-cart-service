package com.example.cart_service.controllers;

import com.example.cart_service.models.Cart;
import com.example.cart_service.services.CartService;
import com.example.cart_service.services.CartServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    CartService cartService;

    @PostMapping("")
    public String createCart(@RequestBody int userId){
        cartService.createCart(userId);
        return "Ok";
    }

    @GetMapping("/{id}")
    public Cart getCart(@PathVariable String id){
        return cartService.findCartById(id);
    }

    @GetMapping("")
    public List<Cart> getAllCart(){
        return cartService.findAllCart();
    }
}
