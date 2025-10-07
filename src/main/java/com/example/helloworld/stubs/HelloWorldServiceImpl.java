//package com.example.helloworld.stubs;
//
//import io.grpc.stub.StreamObserver;
//import net.devh.boot.grpc.server.service.GrpcService;
//
//@GrpcService
//public class HelloWorldServiceImpl extends HelloWorldServiceGrpc.HelloWorldServiceImplBase {
//
//    @Override
//    public StreamObserver<HelloWorldRequest> sayHello(StreamObserver<HelloWorldResponse> responseObserver) {
//
//        // Đây là nơi nhận các request stream từ client
//        return new StreamObserver<HelloWorldRequest>() {
//
//            @Override
//            public void onNext(HelloWorldRequest request) {
//                // Khi client gửi mỗi message
//                String name = request.getName().isEmpty() ? "World" : request.getName();
//                String greeting = "Hello, " + name + "!";
//
//                // Trả về từng message response
//                HelloWorldResponse response = HelloWorldResponse.newBuilder()
//                        .setGreeting(greeting)
//                        .build();
//                responseObserver.onNext(response);
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                System.err.println("Error in stream: " + t.getMessage());
//            }
//
//            @Override
//            public void onCompleted() {
//                // Khi client đóng stream
//                responseObserver.onCompleted();
//            }
//        };
//    }
//}
