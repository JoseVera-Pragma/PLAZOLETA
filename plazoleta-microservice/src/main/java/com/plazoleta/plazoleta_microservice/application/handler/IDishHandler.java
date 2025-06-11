package com.plazoleta.plazoleta_microservice.application.handler;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.domain.util.Page;

public interface IDishHandler {
    DishResponseDto createDish(DishRequestDto dishRequestDto);

    void updateDishPriceAndDescription(Long dishId, DishUpdateRequestDto dishUpdateRequestDto);

    void changeDishStatus(Long dishId, boolean activate);

    Page<DishResponseDto> getDishesByRestaurantAndCategory(Long restaurantId, Long categoryId, int page, int size);
}
