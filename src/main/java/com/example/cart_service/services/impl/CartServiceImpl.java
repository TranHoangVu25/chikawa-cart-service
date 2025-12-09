package com.example.cart_service.services.impl;

import com.example.cart_service.dto.request.CartItemRequest;
import com.example.cart_service.dto.request.OrderRequestDTO;
import com.example.cart_service.dto.response.ApiResponse;
import com.example.cart_service.exception.ErrorCode;
import com.example.cart_service.models.Cart;
import com.example.cart_service.models.CartItem;
import com.example.cart_service.models.Product;
import com.example.cart_service.models.Variant;
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
import java.util.Objects;
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
    public ResponseEntity<ApiResponse<String>> deleteCartItem(Integer userId, String productId, String variantId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean removed;
        if (variantId.isEmpty()) {
            removed = cart.getCartItems().removeIf(
                    item -> ((productId).equals(item.getId())));
        } else {
            removed = cart.getCartItems().removeIf(
                    item -> ((productId + variantId).equals(item.getId() + item.getVariantId()))
            );
        }

        if (!removed) {
            throw new RuntimeException("Product not found in cart");
        }
        cartRepository.save(cart);
        return ResponseEntity.ok()
                .body(
                        ApiResponse.<String>builder()
                                .message("Delete product successfully")
                                .build()
                );
    }
    @Override
    public ResponseEntity<ApiResponse<Cart>> updateQuantity(Integer userId, CartItemRequest request) {
        if (!cartRepository.existsByUserId(userId)) {
            ResponseEntity.badRequest()
                    .body(
                            ApiResponse.<Cart>builder()
                                    .code(ErrorCode.USER_NOT_EXISTED.getCode())
                                    .message(ErrorCode.USER_NOT_EXISTED.getMessage())
                                    .build());
        }
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<CartItem> cartItemList = cart.getCartItems();

        boolean updated = false;
        String reqVariantId = request.getVariantId();
        if(reqVariantId!=null && reqVariantId.isEmpty()) reqVariantId = null;
        for (CartItem item : cartItemList) {
            String itemVariantId = item.getVariantId();

            if(itemVariantId!=null && itemVariantId.isEmpty()) itemVariantId = null;

            boolean sameProd = item.getId().equals(request.getId());
            boolean sameVariantId = Objects.equals(itemVariantId,reqVariantId);

            log.info("sameProd: "+sameProd+" sameVariantId: "+sameVariantId);
            if (sameProd && sameVariantId) {
                {
                    item.setQuantity(request.getQuantity());
                    updated = true;
                    break;
                }
            }
        }

        // nếu k có sản phẩm nào đc update
        if (!updated) {
            log.error("Product not found");
            return ResponseEntity.badRequest()
                    .body(
                            ApiResponse.<Cart>builder()
                                    .code(ErrorCode.PRODUCT_NOT_FOUND.getCode())
                                    .message(ErrorCode.PRODUCT_NOT_FOUND.getMessage())
                                    .build());
        }
        Cart item_update = cartRepository.save(cart);

        return ResponseEntity.ok()
                .body(
                        ApiResponse.<Cart>builder()
                                .message("Update quantity successfully")
                                .result(item_update)
                                .build());
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
        try {
            //kiểm tra trạng thái sản phẩm
        Optional<Product> productOpt = productRepository.findById(request.getId())
                .filter(p -> "available".equalsIgnoreCase(p.getStatus()));

        if (productOpt.isEmpty()) {
            // Product không tồn tại hoặc không available, trả về ApiResponse lỗi
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            ApiResponse.<Cart>builder()
                                    .code(ErrorCode.PRODUCT_NOT_AVAILABLE.getCode())
                                    .message(ErrorCode.PRODUCT_NOT_AVAILABLE.getMessage())
                                    .build());
        }
        //kiểm tra variant co tồn tại không
            String variantId = request.getVariantId();
            log.info("variant id" + variantId);
            if (variantId != null && !variantId.isEmpty()) {
                log.info("in if");
                if (!productRepository.existsByIdAndVariants_Id(request.getId(), variantId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                                ApiResponse.<Cart>builder()
                                        .code(ErrorCode.VARIANT_NOT_FOUND.getCode())
                                        .message(ErrorCode.VARIANT_NOT_FOUND.getMessage())
                                        .build());
                }
            }
        Product product = productOpt.get();
        //Lấy Cart của user (hoặc tạo mới)
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> Cart.builder()
                        .userId(userId)
                        .cartItems(new ArrayList<>())
                        .build());

        // Kiểm tra CartItem đã có product chưa
            //duyệt qua list các product trong cart
        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(ci -> ci.getId().equals(request.getId()))
                .filter(
                        ci -> {
                            String reqVar = request.getVariantId(); //variant id trong request
                            String ciVar = ci.getVariantId(); // variant id khi duyệt trong list

                            // Nếu cả 2 đều null hoặc rỗng -> sp không có variant
                            if ((reqVar == null || reqVar.isEmpty()) &&
                                    (ciVar == null || ciVar.isEmpty())) {
                                return true;
                            }

                            // Nếu cả 2 đều có variantId -> so sánh variantId
                            if (reqVar != null && !reqVar.isEmpty() &&
                                    ciVar != null && !ciVar.isEmpty()) {
                                return reqVar.equals(ciVar);
                            }

                            return false; // trái ngược nhau (1 null, 1 có) -> không match
                        }
                )
                .findFirst();

//        log.info("Variant ID:"+request.getVariantId());

        if (existingItemOpt.isPresent()) {
            //Nếu đã có trong cart, tăng quantity
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else { //Nếu chưa có trong cart, thêm mới vào cart

            // khách chọn sản phẩm có variant
            if (request.getVariantId() != null && !request.getVariantId().isEmpty()) {
                //tìm variant theo variantId
                List<Variant> variants = productRepository.findVariantOnly(request.getVariantId());
                Variant v = variants.get(0);
                log.info("Variant Infor:"+v);
                log.info("Variant ID:"+request.getVariantId());

                CartItem newItem = CartItem.builder()
                        .id(product.getId())
                        .name(v.getName())
                        .price(product.getPrice())
                        .quantity(request.getQuantity())
                        .image(product.getImages() != null && !product.getImages().isEmpty()
                                ? v.getImg()
                                : null)
                        .variantId(request.getVariantId())
                        .build();
                cart.getCartItems().add(newItem);
            }
            else {
                //nếu khách chọn sản phẩm mặc định, or sp k có varient
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
        }

        // Lưu Cart lại
        return ResponseEntity.ok()
                .body(
                        ApiResponse.<Cart>builder()
                                .result(cartRepository.save(cart))
                                .message(ErrorCode.ADD_TO_CART_SUCCESS.getMessage())
                                .build());
    } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}