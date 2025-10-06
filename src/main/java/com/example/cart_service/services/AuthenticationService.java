package com.example.cart_service.services;


import com.example.cart_service.dto.request.AuthenticationRequest;
import com.example.cart_service.dto.request.IntrospectRequest;
import com.example.cart_service.dto.response.AuthenticationResponse;
import com.example.cart_service.dto.response.IntrospectResponse;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationService {

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGN_KEY;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        //thêm khối try catch để nếu verifyToken trả về exception thì trả về false
        try {
            verifyToken(token,false);

        }catch (Exception e){
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    private SignedJWT verifyToken (String token, boolean isRefresh) throws JOSEException, ParseException {
        //Tạo đối tượng để xác minh chữ ký JWT bằng khóa bí mật SIGN_KEY
        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());

        //Phân tích chuỗi token thành đối tượng SignedJWT để truy xuất header, payload và chữ ký
        SignedJWT signedJWT = SignedJWT.parse(token);

        //kiểm tra token hợp lệ
        var verified = signedJWT.verify(verifier);

//        if (!(verified && expiryTime.after(new Date()))) // time hết hạn sau time hiện tại
//        {
//            log.info("Token was expired");
//        }
        return  signedJWT;
    }
}
