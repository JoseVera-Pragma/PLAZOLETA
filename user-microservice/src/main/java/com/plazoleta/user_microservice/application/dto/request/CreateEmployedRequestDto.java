package com.plazoleta.user_microservice.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CreateEmployedRequestDto extends CreateUserRequestDto {
    @NotNull(message = "Restaurant ID must be provided.")
    private Long restaurantId;
}