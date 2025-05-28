package com.plazoleta.plazoleta_microservice.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DishData {
    private String name;
    private Double price;
    private String description;
    private String imageUrl;
    private String categoryName;
}
