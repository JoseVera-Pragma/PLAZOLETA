package com.plazoleta.plazoleta_microservice.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateOrderRequestDto {
    @NotNull(message = "The Restaurant id cannot be empty")
    private Long idRestaurant;

    @NotNull(message = "The dishes cannot be empty")
    @Size(min = 1, message = "At least one dish must be provided")
    @Valid
    ArrayList<DishOrderRequestDto> dishes;
}
