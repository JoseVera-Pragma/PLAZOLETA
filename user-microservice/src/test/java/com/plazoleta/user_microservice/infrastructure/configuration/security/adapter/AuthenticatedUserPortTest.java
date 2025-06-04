package com.plazoleta.user_microservice.infrastructure.configuration.security.adapter;

import com.plazoleta.user_microservice.domain.api.IRoleServicePort;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserPortTest {

    @Mock
    private IRoleServicePort iRoleServicePort;

    private AuthenticatedUserPort authenticatedUserPort;

    @BeforeEach
    void setUp() {
        authenticatedUserPort = new AuthenticatedUserPort(iRoleServicePort);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserId_present() {
        Map<String, Object> details = Map.of("userId", 123L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getDetails()).thenReturn(details);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        Optional<Long> result = authenticatedUserPort.getCurrentUserId();

        assertTrue(result.isPresent());
        assertEquals(123L, result.get());
    }

    @Test
    void testGetCurrentUserId_absent() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getDetails()).thenReturn(Map.of());

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        Optional<Long> result = authenticatedUserPort.getCurrentUserId();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetCurrentUserRole_validAuthority() {
        GrantedAuthority authority = () -> "ROLE_OWNER";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getAuthorities()).thenReturn((List) List.of(authority));

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        Role expectedRole = new Role(2L, RoleList.ROLE_OWNER,"ROLE_OWNER");
        when(iRoleServicePort.getRoleByName(RoleList.ROLE_OWNER)).thenReturn(expectedRole);

        Role result = authenticatedUserPort.getCurrentUserRole();

        assertEquals(expectedRole, result);
    }

    @Test
    void testGetCurrentUserRole_invalidAuthority_fallbackToCustomer() {
        GrantedAuthority authority = () -> "INVALID_ROLE";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getAuthorities()).thenReturn((List) List.of(authority));

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        Role fallbackRole = new Role(1L, RoleList.ROLE_CUSTOMER,"ROLE_CUSTOMER");
        when(iRoleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(fallbackRole);

        Role result = authenticatedUserPort.getCurrentUserRole();

        assertEquals(fallbackRole, result);
    }

    @Test
    void testGetCurrentUserRole_noAuthorities_returnsCustomer() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getAuthorities()).thenReturn(null);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        Role fallbackRole = new Role(1L, RoleList.ROLE_CUSTOMER,"ROLE_CUSTOMER");
        when(iRoleServicePort.getRoleByName(RoleList.ROLE_CUSTOMER)).thenReturn(fallbackRole);

        Role result = authenticatedUserPort.getCurrentUserRole();

        assertEquals(fallbackRole, result);
    }
}