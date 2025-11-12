package com.example.cart_service.controllers;

import com.example.cart_service.dto.request.CartItemRequest;
import com.example.cart_service.dto.response.ApiResponse;
//import com.example.cart_service.grpc.GrpcOrderClient;
import com.example.cart_service.grpc.GrpcOrderClient;
import com.example.cart_service.models.Cart;
import com.example.cart_service.services.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    CartService cartService;
    GrpcOrderClient grpcOrderClient;


    //tạo cart theo id user (nếu chưa có)
    //sửa lại thành get nếu chưa có
    @GetMapping("")
    public ResponseEntity createCart(@AuthenticationPrincipal Jwt jwt) {
        Integer userId = Integer.valueOf(jwt.getClaimAsString("sub"));
        Cart cart = cartService.findByUserIdToCreate(userId);
        if (cart == null) {
            cartService.createCart(userId);
            return ResponseEntity.ok("Cart created for user " + userId);
        }
        return ResponseEntity.ok("Cart already exists for user " + userId);
    }

    //tìm kiếm cart theo user id
    @GetMapping("/get-user-cart")
    public Cart getCart(@AuthenticationPrincipal Jwt jwt) {
        Integer userId = Integer.valueOf(jwt.getClaimAsString("sub"));
        return cartService.findByUserId(userId);
    }

    //hiển thị tất cả các giỏ hàng của user
    @GetMapping("/get-carts")
    public List<Cart> getAllCart() {
        return cartService.findAllCart();
    }

    @PostMapping
    public Cart createCartItem(@RequestBody @Valid com.example.cart_service.models.CartItem cartItem,
            @AuthenticationPrincipal Jwt jwt ) {
        Integer userId = Integer.parseInt(jwt.getClaimAsString("sub"));

        System.out.println("✅ Thêm sản phẩm từ product service:");
        System.out.println("Items: " + cartItem.getName());

        return cartService.createCartItem(userId, cartItem);
    }

    //xóa cart item khỏi cart
    @DeleteMapping("/cart-items/{productId}")
    public Cart deleteCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String productId
    ) {
        Integer userId = Integer.valueOf(jwt.getClaimAsString("sub"));
        return cartService.deleteCartItem(userId, productId);
    }

    //sửa số lượng sản phẩm
    @PutMapping("/cart-items")
    public Cart updateCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CartItemRequest request
    ) {
        Integer userId = Integer.valueOf(jwt.getClaimAsString("sub"));
        return cartService.updateQuantity(userId, request);
    }

    //checkout: nếu thanh toán thành công thì xóa toàn bộ cart hiện tại
    // nếu không thành công thì cart vẫn để nguyên
    @GetMapping("/checkout")
    public ApiResponse<String> checkout(@AuthenticationPrincipal Jwt jwt) {
        Integer userId = Integer.valueOf(jwt.getClaimAsString("sub"));
        return cartService.checkout(userId,jwt.getTokenValue());
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<ApiResponse<Cart>> addToCart(
            @RequestBody @Valid CartItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        try {
            Integer userId = Integer.valueOf(jwt.getClaimAsString("sub"));
            return cartService.addToCart(userId,request);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/checkout")
    public String checkout(@RequestBody Cart cart) {

        if (cart == null || cart.getCartItems().isEmpty()) {
            return "Error: Cart is empty or invalid.";
        }

        try {
            // Gọi gRPC Client, chỉ truyền dữ liệu cần thiết cho OrderService
            String orderResponse = grpcOrderClient.sendOrder(
                    cart.getUserId(),
                    cart.getCartItems() // Dữ liệu giỏ hàng
            );

            return "Checkout successful. Order Service Response: " + orderResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during gRPC communication: " + e.getMessage();
        }
    }
}
