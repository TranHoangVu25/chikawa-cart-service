package com.example.cart_service.grpc;


import com.example.cart_service.models.DeliveryAddress;
import com.example.grpc.CartItem;
import com.example.grpc.CartRequest;
import com.example.grpc.OrderResponse;
import com.example.grpc.OrderServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GrpcOrderClient {

    @GrpcClient("orderService")
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

    public OrderResponse sendOrder(
            int userId,
            List<com.example.cart_service.dto.request.CheckoutItemRequest > cartItems,
            DeliveryAddress address
    ) {
        CartRequest.Builder requestBuilder = CartRequest.newBuilder()
                .setUserId(userId);


        cartItems.forEach(item ->
                requestBuilder.addItems(
                        com.example.grpc.CartItem.newBuilder()
                                .setId(item.getId())
                                .setName(item.getName())
                                .setPrice(item.getPrice())
                                .setQuantity(item.getQuantity())
                                .setImage(item.getImage())
                                .setVariantId(
                                        item.getVariantId() == null ? "" : item.getVariantId()
                                )
                                .build()
                )
        );

        log.info("In GRPC Client Service.List item: "+cartItems);
        if(address != null) {
            com.example.grpc.DeliveryAddress deliveryAddress =
                    com.example.grpc.DeliveryAddress.newBuilder()
                            .setRecipientName(address.getRecipientName())
                            .setPhoneNumber(address.getPhoneNumber())
                            .setCountry(address.getCountry())
                            .setProvince(address.getProvince())
                            .setCity(address.getCity())
                            .setLocationDetail(address.getLocationDetail())
                            .build();

            requestBuilder.setAddress(deliveryAddress);
        }

        return orderServiceStub.createOrder(requestBuilder.build());
    }
}
