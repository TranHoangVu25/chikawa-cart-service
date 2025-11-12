package com.example.cart_service.grpc;


import com.example.grpc.CartItem;
import com.example.grpc.CartRequest;
import com.example.grpc.OrderResponse;
import com.example.grpc.OrderServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class GrpcOrderClient {

    // Inject Blocking Stub để gọi đồng bộ
    @GrpcClient("orderService")
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

    // Bạn sẽ phải tạo lớp CartItem trong package models/ bên CartService
    public String sendOrder(int userId, java.util.List<com.example.cart_service.models.CartItem> cartItems) {

        // 1. Xây dựng CartRequest từ dữ liệu local
        CartRequest.Builder requestBuilder = CartRequest.newBuilder()
                .setUserId(userId);

        // Chuyển đổi từ CartItem local sang CartItem Protobuf
        cartItems.forEach(item -> {
            requestBuilder.addItems(CartItem.newBuilder()
                    .setId(item.getId())
                    .setName(item.getName())
                    .setPrice(item.getPrice())
                    .setQuantity(item.getQuantity())
                    .setImage(item.getImage())
                    .build());
        });

        // 2. Gọi gRPC Server
        OrderResponse response = orderServiceStub.createOrder(requestBuilder.build());

        // 3. Trả về kết quả
        return response.getMessage();
    }
}