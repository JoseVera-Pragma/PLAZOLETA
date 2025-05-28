package com.plazoleta.plazoleta_microservice.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;
}
