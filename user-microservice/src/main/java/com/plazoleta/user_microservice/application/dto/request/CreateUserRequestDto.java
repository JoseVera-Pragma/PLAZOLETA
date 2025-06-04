package com.plazoleta.user_microservice.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CreateUserRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Identity number is required")
    @Pattern(regexp = "^\\d+$", message = "Identity number must contain only digits")
    private String identityNumber;

    @NotBlank(message = "Phone number is required")
    @Size(max = 13, message = "Phone number is extensive max 13 characters")
    @Pattern(regexp = "^\\+?\\d+$", message = "Phone number is not valid")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
