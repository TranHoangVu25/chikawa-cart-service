package com.example.cart_service.controllers;

import com.example.cart_service.dto.request.CartItemRequest;
import com.example.cart_service.models.Cart;
import com.example.cart_service.models.CartItem;
import com.example.cart_service.services.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    @PostMapping("/cart-items/{userId}")
    public Cart createCartItem(
            @RequestBody CartItem cartItem,
            @PathVariable int userId
            ){
        return cartService.createCartItem(userId,cartItem);
    }

    @DeleteMapping("/cart-items/{userId}/{productId}")
    public Cart deleteCartItem(
            @PathVariable Integer userId,
            @PathVariable String productId
    ){
        return cartService.deleteCartItem(userId,productId);
    }

    @PutMapping("cart-items/{userId}")
    public Cart updateCartItem(
            @PathVariable Integer userId,
            @RequestBody CartItemRequest request
            ){
        return cartService.updateQuantity(userId,request);
    }
}
