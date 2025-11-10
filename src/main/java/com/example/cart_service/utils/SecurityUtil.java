package com.example.cart_service.utils;

import com.example.cart_service.models.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    /**
     * Set user info vào SecurityContextHolder
     */
    public static void setAuthentication(String userId, String username) {
        // Tạo principal từ userId và username
        CustomUserDetails userDetails = new CustomUserDetails(userId, username);

        // Tạo Authentication token (credentials là null, authorities nếu có)
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // Set vào SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Lấy userId hiện tại từ SecurityContextHolder
     */
    public static String getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }

        if (principal instanceof String) {
            return (String) principal;
        }

        return null;
    }
}
