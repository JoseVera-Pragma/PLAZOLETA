package com.plazoleta.plazoleta_microservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@Builder
public class CategoryRequestDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;
}
