package com.example.cart_service.repositories;

import com.example.cart_service.dto.response.ApiResponse;
import com.example.cart_service.models.Cart;
import com.example.cart_service.models.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart,String> {
    Optional<Cart> findById(String id);

    //trả về cart theo user_id
    @Query("{ 'userId': ?0 }")
    Optional<Cart> findByUserId(Integer userId);

    boolean existsByUserId(Integer userId);

    Optional<CartItem> findByCartItems_Id(String cartItemId);


}
