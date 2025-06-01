package com.plazoleta.plazoleta_microservice.application.handler;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import org.springframework.data.domain.Page;

public interface IDishHandler {
    DishResponseDto createDish(Long restaurantId, DishRequestDto dishRequestDto);
    void updateDish(Long dishId, DishUpdateRequestDto dishUpdateRequestDto);

    void changeDishStatus(Long dishId, boolean activate);

    Page<DishResponseDto> getDishesByRestaurantAndCategory(Long restaurantId, Long categoryId, int page, int size);
}
