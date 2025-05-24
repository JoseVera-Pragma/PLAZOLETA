package com.plazoleta.user_microservice.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequestDto {

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

    @NotNull(message = "Date of Birth is required")
    @Past(message = "Date of birth is not valid")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
