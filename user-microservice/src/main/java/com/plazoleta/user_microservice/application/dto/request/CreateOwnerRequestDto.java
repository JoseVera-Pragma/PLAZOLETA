package com.plazoleta.user_microservice.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CreateOwnerRequestDto extends CreateUserRequestDto {

    @NotNull(message = "Date of Birth is required")
    @Past(message = "Date of birth is not valid")
    private LocalDate dateOfBirth;
}
