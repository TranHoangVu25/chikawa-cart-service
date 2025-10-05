package com.example.cart_service.repositories;

import com.example.cart_service.models.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartItemRepository extends MongoRepository<CartItem,String> {

}
