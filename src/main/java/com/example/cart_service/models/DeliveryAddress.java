package com.example.cart_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAddress {
    private String recipientName;
    private String phoneNumber;
    private String country;
    private String province;
    private String city;
    private String locationDetail;
}

