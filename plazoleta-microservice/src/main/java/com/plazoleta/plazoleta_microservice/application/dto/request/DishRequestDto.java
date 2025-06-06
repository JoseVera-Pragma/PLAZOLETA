package com.plazoleta.plazoleta_microservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DishRequestDto {

    @NotNull(message = "Restaurant Id is required")
    private Long restaurantId;

    @NotBlank(message = "Dish name is required")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Image url is required")
    private String imageUrl;

    @NotBlank(message = "Category name is required")
    private String categoryName;
}
