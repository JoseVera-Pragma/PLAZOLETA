package com.plazoleta.plazoleta_microservice.application.handler;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;

import java.util.List;

public interface IDishHandler {
    DishResponseDto createDish(Long restaurantId, DishRequestDto dishRequestDto);
    void updateDish(Long dishId, DishUpdateRequestDto dishUpdateRequestDto);
}
