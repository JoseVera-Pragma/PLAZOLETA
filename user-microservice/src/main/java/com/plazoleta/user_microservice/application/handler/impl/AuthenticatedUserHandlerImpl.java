package com.plazoleta.user_microservice.application.handler.impl;

import com.plazoleta.user_microservice.application.handler.IAuthenticatedUserHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticatedUserHandlerImpl implements IAuthenticatedUserHandler {

    @Override
    public Optional<Long> getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof Map<?, ?> detailsMap) {

            Object userId = detailsMap.get("userId");
            if (userId != null) {
                return Optional.of(Long.parseLong(userId.toString()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            return auth.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority);
        }
        return Optional.empty();
    }

}
