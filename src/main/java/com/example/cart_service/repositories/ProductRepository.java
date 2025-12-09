package com.example.cart_service.repositories;

import com.example.cart_service.models.Product;
import com.example.cart_service.models.Variant;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product,String> {
    @Aggregation(pipeline = {
            "{ $unwind: '$variants' }",
            "{ $match: { 'variants.id': ?0 } }",
            "{ $project: { _id: 0, id: '$variants.id', name: '$variants.name', img: '$variants.img' } }"
    })
    List<Variant> findVariantOnly(String variantId);

    boolean existsByVariants_Id(String variantId);

    boolean existsByIdAndVariants_Id(String productId, String variantId);

}
