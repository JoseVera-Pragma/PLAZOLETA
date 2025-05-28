package com.plazoleta.plazoleta_microservice.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DishResponseDto {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String categoryName;
    private boolean active;
    private Long restaurantId;
}
