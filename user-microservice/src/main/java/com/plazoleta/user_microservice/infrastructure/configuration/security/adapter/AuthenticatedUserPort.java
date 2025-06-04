package com.plazoleta.user_microservice.infrastructure.configuration.security.adapter;

import com.plazoleta.user_microservice.domain.api.IRoleServicePort;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.domain.spi.IAuthenticatedUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserPort implements IAuthenticatedUserPort {
    private final IRoleServicePort iRoleServicePort;

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
    public Role getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getAuthorities() != null) {
            return auth.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .flatMap(roleStr -> {
                        try {
                            RoleList roleList = RoleList.valueOf(roleStr);
                            return Optional.of(iRoleServicePort.getRoleByName(roleList));
                        } catch (IllegalArgumentException e) {
                            return Optional.empty();
                        }
                    })
                    .orElseGet(() -> iRoleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER));
        }

        return iRoleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER);
    }
}
