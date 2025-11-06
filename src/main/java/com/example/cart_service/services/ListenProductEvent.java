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
    @RabbitListener(queues = "search_service_queue")
    public void listenCreateProduct(ProductDTO event) {
        System.out.println("Received: " + event.getName());

        Product prod = Product.builder()
                .id(event.getId())
                .name(event.getName())
                .price(event.getPrice())
                .status(event.getStatus())
                .images(event.getImages())
                .build();

        productRepository.save(prod);
        System.out.println("Indexed product in Elasticsearch: " + event.getId());
    }
}
