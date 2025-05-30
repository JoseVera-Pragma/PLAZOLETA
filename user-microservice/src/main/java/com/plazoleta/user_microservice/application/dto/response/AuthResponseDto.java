package com.plazoleta.user_microservice.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

    @Schema(description = "User email", example = "admin@gmail.com")
    private String email;

    @Schema(description = "User role", example = "ROLE_ADMIN")
    private String role;

    @Schema(description = "User ID", example = "1")
    private Long userId;
}