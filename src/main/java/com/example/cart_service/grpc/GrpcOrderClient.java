package com.example.cart_service.grpc;


import com.example.grpc.CartItem;
import com.example.grpc.CartRequest;
import com.example.grpc.OrderResponse;
import com.example.grpc.OrderServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrpcOrderClient {

    @GrpcClient("orderService")
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

    public OrderResponse sendOrder(
            int userId,
            List<com.example.cart_service.dto.request.CheckoutItemRequest > cartItems
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

        return orderServiceStub.createOrder(requestBuilder.build());
    }
}
