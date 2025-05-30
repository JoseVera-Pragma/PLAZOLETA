package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.handler.IAuthenticatedUserHandler;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.model.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserHandlerImpl implements IAuthenticatedUserHandler {

    @Override
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null ) {
            CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
            return principal.id();
        }
        throw new UserNotFoundException("The authenticated user ID could not be obtained");
    }

}
