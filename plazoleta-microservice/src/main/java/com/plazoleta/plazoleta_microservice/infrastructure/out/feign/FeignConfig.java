package com.plazoleta.plazoleta_microservice.infrastructure.out.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

public class FeignConfig {

    @Bean
    public RequestInterceptor authRequestInterceptor() {
        return requestTemplate -> {
            var context = SecurityContextHolder.getContext();
            if (context == null) return;

            var authentication = context.getAuthentication();
            if (authentication == null) return;

            Object credentials = authentication.getCredentials();
            if (credentials instanceof String token && token.startsWith("ey")) {
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
