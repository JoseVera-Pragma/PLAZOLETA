package com.plazoleta.plazoleta_microservice.application.handler;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;

import java.util.List;

public interface IDishHandler {
    DishResponseDto createDish(DishRequestDto dishRequestDto);

    void updateDishPriceAndDescription(Long dishId, DishUpdateRequestDto dishUpdateRequestDto);

    void changeDishStatus(Long dishId, boolean activate);

    List<DishResponseDto> getDishesByRestaurantAndCategory(Long restaurantId,Long categoryId, int page, int size);
}
