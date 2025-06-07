package com.plazoleta.sms_service.infrastructure.configuration.security.filter;

import com.plazoleta.sms_service.infrastructure.configuration.security.adapter.JwtTokenAdapter;
import com.plazoleta.sms_service.infrastructure.configuration.security.model.CustomUserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter implements Filter {

    private final JwtTokenAdapter jwtTokenAdapter;

    public JwtRequestFilter(JwtTokenAdapter jwtTokenAdapter) {
        this.jwtTokenAdapter = jwtTokenAdapter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");

            if (jwtTokenAdapter.validateToken(token)) {
                Claims claims = jwtTokenAdapter.extractClaims(token);
                String role = claims.get("role", String.class);
                Long userId = claims.get("userId", Long.class);
                String email = claims.getSubject();

                CustomUserPrincipal principal = new CustomUserPrincipal(userId, email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority(role))
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}