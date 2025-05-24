package com.plazoleta.user_microservice.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserResponseDto {
    private String firstName;
    private String lastName;
    private String identityNumber;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String role;
}
