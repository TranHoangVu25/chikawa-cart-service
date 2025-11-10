package com.example.cart_service.services;

import com.example.cart_service.dto.ProductDTO;
import com.example.cart_service.models.Product;
import com.example.cart_service.repositories.CartRepository;
import com.example.cart_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListenProductEvent {
    private final CartRepository repository;
    private final ProductRepository productRepository;

    //receive data from create product
    public void listenCreateProduct(ProductDTO event) {
        try {
            String img = null;
            if(event.getImages()!=null){
                 img = event.getImages().get(0);
            }
            System.out.println("Received: " + event.getName());

            Product prod = Product.builder()
                    .id(event.getId())
                    .name(event.getName())
                    .price(event.getPrice())
                    .status(event.getStatus())
                    .images(img)
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
                System.out.println("Received: " + event.getName());
                Product d = productRepository.findById(event.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                d.setName(event.getName());
                d.setStatus(event.getStatus());
                d.setPrice(event.getPrice());
                d.setImages(img);

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
        log.info("📥 Received raw event: {}", e); // log toàn bộ object
        switch (e.getAction()){
            case CREATE -> listenCreateProduct(e);
            case UPDATE -> listenUpdateProduct(e);
            case DELETE -> listenDeleteProduct(e);
            case null, default -> throw new RuntimeException("In receive data. Error in receive!!");
        }
    }
}
