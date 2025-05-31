package com.plazoleta.plazoleta_microservice.application.handler;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;

public interface IDishHandler {
    DishResponseDto createDish(Long restaurantId, DishRequestDto dishRequestDto);
    void updateDish(Long dishId, DishUpdateRequestDto dishUpdateRequestDto);

    void changeDishStatus(Long dishId, boolean activate);
}
