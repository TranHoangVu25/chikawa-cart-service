package com.example.cart_service.services.impl;

import com.example.cart_service.dto.request.CartItemRequest;
import com.example.cart_service.dto.request.OrderRequestDTO;
import com.example.cart_service.dto.response.ApiResponse;
import com.example.cart_service.exception.ErrorCode;
import com.example.cart_service.models.Cart;
import com.example.cart_service.models.CartItem;
import com.example.cart_service.models.Product;
import com.example.cart_service.repositories.CartRepository;
import com.example.cart_service.repositories.ProductRepository;
import com.example.cart_service.services.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {

    CartRepository cartRepository;
    ProductRepository productRepository;
    RestTemplate restTemplate;
    String ORDER_SERVICE_URL = "http://localhost:8081/api/v1/order";

    @Override
    public void createCart(Integer userId) {
        List<com.example.cart_service.models.CartItem> cartItemList = new ArrayList<>();
        Cart cart = new Cart()
                .builder()
                .userId(userId)
                .cartItems(cartItemList)
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

    //thêm cart item vào giỏ hàng
    @Override
    public Cart createCartItem(Integer userId, com.example.cart_service.models.CartItem newItem) {
        boolean a = cartRepository.existsByUserId(userId);
        log.info("result ="+a);
        //check giỏ hàng đã tồn tại chưa, nếu chưa thì tạo mới
        if (!cartRepository.existsByUserId(userId)){
            List<com.example.cart_service.models.CartItem> cartItemList = new ArrayList<>();
            Cart cart = new Cart()
                    .builder()
                    .userId(userId)
                    .cartItems(cartItemList)
                    .build();
            cartRepository.save(cart);
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User have id: "+userId+" not found cart."));

        List<com.example.cart_service.models.CartItem> existingCartItems = cart.getCartItems();

        boolean found = false;

        for (com.example.cart_service.models.CartItem item : existingCartItems) {
            if (newItem.getId().equals(item.getId())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                item.setPrice(item.getPrice()* item.getQuantity());
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
        boolean removed = cart.getCartItems().removeIf(
                item -> productId.equals(item.getId())
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
        List<com.example.cart_service.models.CartItem> cartItemList = cart.getCartItems();

        boolean updated = false;

        for (com.example.cart_service.models.CartItem item : cartItemList) {
            if (item.getId().equals(request.getId())) {
                item.setQuantity(request.getQuantity());
                item.setPrice(request.getQuantity()*item.getPrice());
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

        List<com.example.cart_service.models.CartItem> cartItems = cart.getCartItems();

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

    @Override
    public ResponseEntity<ApiResponse<Cart>> addToCart(Integer userId, CartItemRequest request) {
        Optional<Product> productOpt = productRepository.findById(request.getId())
                .filter(p -> "available".equalsIgnoreCase(p.getStatus()));

        if (productOpt.isEmpty()) {
            // Product không tồn tại hoặc không available, trả về ApiResponse lỗi
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            ApiResponse.<Cart>builder()
                                    .code(404) // hoặc code phù hợp
                                    .message(ErrorCode.PRODUCT_NOT_AVAILABLE.getMessage())
                                    .build());
        }

        Product product = productOpt.get();
        //Lấy Cart của user (hoặc tạo mới)
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> Cart.builder()
                        .userId(userId)
                        .cartItems(new ArrayList<>())
                        .build());

        // Kiểm tra CartItem đã có product chưa
        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(ci -> ci.getId().equals(request.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            //Nếu đã có, tăng quantity
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            //Nếu chưa có, thêm mới
            CartItem newItem = CartItem.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(request.getQuantity())
                    .image(product.getImages() != null && !product.getImages().isEmpty()
                            ? product.getImages().get(0)
                            : null)
                    .build();
            cart.getCartItems().add(newItem);
        }

        // Lưu Cart lại
        return ResponseEntity.ok()
                .body(
                        ApiResponse.<Cart>builder()
                                .result(cartRepository.save(cart))
                                .message(ErrorCode.ADD_TO_CART_SUCCESS.getMessage())
                                .build());
    }
}