package com.example.cart_service.repositories;

import com.example.cart_service.models.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart,String> {
    Optional<Cart> findById(String id);

    @Query("{ 'user_id': ?0 }")
    Optional<Cart> findByUserId(Integer userId);
}
