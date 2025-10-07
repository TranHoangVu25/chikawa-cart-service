package com.example.cart_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)//entity nào ở dưới = null thì k cần hiển thị
public class ApiResponse <T> {
    int code=1000;
    String message;
    T result;
}
