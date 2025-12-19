package com.example.cart_service.dto.request;

import com.example.cart_service.models.DeliveryAddress;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartCheckOutRequest {
    List<CheckoutItemRequest> items;
    DeliveryAddress address;
    //String promotion;
}
