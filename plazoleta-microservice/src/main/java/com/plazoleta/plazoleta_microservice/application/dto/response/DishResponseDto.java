package com.plazoleta.plazoleta_microservice.application.dto.response;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
