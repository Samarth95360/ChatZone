package com.Chat_App.Chat_Service.Config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignClientAuthConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getCredentials() instanceof String token) {
                System.out.println("Passing token from SecurityContext: " + token);
                template.header("Authorization", "Bearer " + token);
            } else {
                System.out.println("No auth found in SecurityContextHolder or invalid token.");
            }
        };
    }

}
