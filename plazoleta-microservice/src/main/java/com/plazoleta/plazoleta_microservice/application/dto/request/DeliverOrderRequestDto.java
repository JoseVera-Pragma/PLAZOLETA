package com.plazoleta.plazoleta_microservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliverOrderRequestDto {
    @NotBlank(message = "Pin cannot be empty.")
    @Size(min = 4, message = "Pin must have at least 4 characters.")
    private String pin;
}
