package com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.adapter;

import com.plazoleta.plazoleta_microservice.domain.spi.IAuthenticatedUserPort;
import com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.model.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticatedUserPort implements IAuthenticatedUserPort {

    @Override
    public Optional<Long> getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof CustomUserPrincipal principal) {
            return Optional.of(principal.id());
        }

        return Optional.empty();
    }

}
