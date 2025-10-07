package com.example.cart_service.services;

import com.example.cart_service.dto.CartItemDTO;
import com.example.cart_service.dto.request.CartItemRequest;
import com.example.cart_service.dto.request.OrderRequestDTO;
import com.example.cart_service.dto.response.ApiResponse;
import com.example.cart_service.models.Cart;
import com.example.cart_service.models.CartItem;
import com.example.cart_service.repositories.CartRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {

    CartRepository cartRepository;
    RestTemplate restTemplate;
    String ORDER_SERVICE_URL = "http://localhost:8081/api/v1/order";


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
                .orElseThrow(() -> new RuntimeException("cart id not found"));
    }

    @Override
    public List<Cart> findAllCart() {
        return cartRepository.findAll();
    }

    @Override
    public Cart createCartItem(Integer userId, CartItem newItem) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> existingCartItems = cart.getCart_items();

        boolean found = false;

        for (CartItem item : existingCartItems) {
            if (newItem.getProduct_id().equals(item.getProduct_id())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) existingCartItems.add(newItem);
        return cartRepository.save(cart);
    }

    @Override
    public Cart deleteCartItem(Integer userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean removed = cart.getCart_items().removeIf(
                item -> productId.equals(item.getProduct_id())
        );

        if (!removed) {
            throw new RuntimeException("Product not found in cart");
        }
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateQuantity(Integer userId, CartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<CartItem> cartItemList = cart.getCart_items();

        boolean updated = false;

        for (CartItem item : cartItemList) {
            if (item.getProduct_id().equals(request.getProduct_id())) {
                item.setQuantity(request.getQuantity());
                updated = true;
                break;
            }
        }
        if (!updated) throw new RuntimeException("Product not found");
        return cartRepository.save(cart);
    }

    @Override
    public Cart findByUserId(Integer userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User id not found"));
    }

    @Override
    public Cart findByUserIdToCreate(Integer userId) {
        return cartRepository.findByUserId(userId)
                .orElse(null);
    }

    @Override
    public ApiResponse<String> checkout(Integer userId, String jwtToken) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("id not found"));

        List<CartItem> cartItems = cart.getCart_items();

        if (cartItems == null || cartItems.isEmpty()) {
            return ApiResponse.<String>builder()
                    .code(400)
                    .message("Giỏ hàng trống, không thể tạo đơn hàng")
                    .build();
        }
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setUserId(userId);
        orderRequest.setItems(cartItems);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);  // truyền JWT qua header

        HttpEntity<OrderRequestDTO> requestEntity = new HttpEntity<>(orderRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                ORDER_SERVICE_URL, requestEntity, String.class
        );

        return ApiResponse.<String>builder()
                .message("Đã gửi dữ liệu sang order_service")
                .result(response.getBody())
                .build();
    }
}
