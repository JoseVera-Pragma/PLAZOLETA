package com.plazoleta.user_microservice.infrastructure.input.rest;

import com.plazoleta.user_microservice.application.dto.request.LoginRequestDto;
import com.plazoleta.user_microservice.application.dto.response.AuthResponseDto;
import com.plazoleta.user_microservice.application.mapper.IAuthRequestMapper;
import com.plazoleta.user_microservice.domain.api.IAuthServicePort;
import com.plazoleta.user_microservice.domain.model.AuthToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthServicePort authServicePort;
    private final IAuthRequestMapper authRequestMapper;

    @Operation(summary = "Authenticate user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        AuthToken authToken = authServicePort.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        AuthResponseDto response = authRequestMapper.toAuthResponse(authToken);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validate JWT token")
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        boolean isValid = authServicePort.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}