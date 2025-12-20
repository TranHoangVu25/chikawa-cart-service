package com.example.cart_service.services;

import com.example.cart_service.dto.CartItemDeleteEvent;
import com.example.cart_service.dto.ProductDTO;
import com.example.cart_service.enums.Action;
import com.example.cart_service.models.Cart;
import com.example.cart_service.models.CartItem;
import com.example.cart_service.models.Product;
import com.example.cart_service.repositories.CartRepository;
import com.example.cart_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListenProductServiceImpl{
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    //receive data from create product
    public void listenCreateProduct(ProductDTO event) {
        try {
            String img = null;
            if(event.getImages()!=null){
                 img = event.getImages().get(0);
            }
            List<String> imgs = new ArrayList<>();
            imgs.add(img);
            System.out.println("Received: " + event.getName());

            Product prod = Product.builder()
                    .id(event.getId())
                    .name(event.getName())
                    .price(event.getPrice())
                    .status(event.getStatus())
                    .images(imgs)
                    .build();

            productRepository.save(prod);
            System.out.println("Indexed product in cart: " + event.getId());
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenUpdateProduct(ProductDTO event) {
        try {
            String img = null;
            if(event.getImages()!=null){
                img = event.getImages().get(0);
            }
            List<String> imgs = new ArrayList<>();
            imgs.add(img);
                System.out.println("Received: " + event.getName());
                Product d = productRepository.findById(event.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                d.setName(event.getName());
                d.setStatus(event.getStatus());
                d.setPrice(event.getPrice());
                d.setImages(imgs);

                productRepository.save(d);
                System.out.println("Indexed product in cart: " + event.getId());

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenDeleteProduct(ProductDTO event) {
        try {
                System.out.println("Received: " + event.getName());
                productRepository.findById(event.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                productRepository.deleteById(event.getId());
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "product_cart_queue")
    public void receiveData(ProductDTO e){
        log.info("Received raw event: {}", e); // log toàn bộ object
        switch (e.getAction()){
            case CREATE -> listenCreateProduct(e);
            case UPDATE -> listenUpdateProduct(e);
            case DELETE -> listenDeleteProduct(e);
            case null, default -> throw new RuntimeException("In receive data. Error in receive!!");
        }
    }
    @RabbitListener(queues = "order_cart_queue")
    @Transactional
    public void deleteCartItem(CartItemDeleteEvent e) {
        log.info("Received CartItemDeleteEvent: {}", e);

        if (e.getAction() != Action.DELETE_CART_ITEMS) {
            return;
        }

        Cart cart = cartRepository.findByUserId(e.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cart.getCartItems();

        List<CartItem> orderItems = e.getOrderItems();

        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem cartItem = iterator.next();

            for (CartItem orderItem : orderItems) {
                if (cartItem.getId().equals(orderItem.getId())) {
                    iterator.remove(); // xóa an toàn
                    break;
                }
            }
        }

        cartRepository.save(cart);

        log.info("Deleted ordered items from cart for user {}", e.getUserId());
    }


}
