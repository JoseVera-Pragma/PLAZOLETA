package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishResponseMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishUpdateRequestMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IDishServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandlerImpl implements IDishHandler {
    private final IDishServicePort dishServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishUpdateRequestMapper updateRequestMapper;
    private final IDishResponseMapper dishResponseMapper;

    @Override
    public DishResponseDto createDish(DishRequestDto dishRequestDto) {
        Dish dish = dishRequestMapper.toDish(dishRequestDto);
        return dishResponseMapper.toDishResponse(dishServicePort.saveDish(dish));
    }

    @Override
    public void updateDishPriceAndDescription(Long dishId, DishUpdateRequestDto dishUpdateRequestDto) {
        dishServicePort.updateDishPriceAndDescription(dishId, updateRequestMapper.toDish(dishUpdateRequestDto));
    }

    @Override
    public void changeDishStatus(Long dishId, boolean activate) {
        dishServicePort.changeDishStatus(dishId, activate);
    }

    @Override
    public Page<DishResponseDto> getDishesByRestaurantAndCategory(Long restaurantId, Long categoryId, int pageIndex, int elementsPerPage) {
        return dishResponseMapper.toDishDtoPage(dishServicePort.findAllDishesByRestaurantIdAndCategoryId(restaurantId, categoryId, pageIndex, elementsPerPage));
    }
}
