package com.example.cart_service.models;

import com.example.cart_service.validator.PriceConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CartItem {
    String product_id;
    @Size(message = "INVALID_CART_ITEM_NAME",min = 3)
    String product_name;
    @PriceConstraint(min = 0)
    double price;
    @Min(message = "INVALID_QUANTITY",value = 1)
    int quantity;
    String img_url;
}
