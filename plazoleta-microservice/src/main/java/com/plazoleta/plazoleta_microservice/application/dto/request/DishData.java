package com.plazoleta.plazoleta_microservice.application.dto.request;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DishData {
    private String name;
    private Double price;
    private String description;
    private String imageUrl;
    private String categoryName;
}
