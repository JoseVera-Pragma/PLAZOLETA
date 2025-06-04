package com.plazoleta.user_microservice.application.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private String firstName;
    private String lastName;
    private String identityNumber;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String role;
    private Long restaurantId;
}
