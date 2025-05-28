package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishData;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.ICategoryServicePort;
import com.plazoleta.plazoleta_microservice.domain.api.IDishServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandlerImpl implements IDishHandler {
    private final IDishServicePort dishServicePort;
    private final ICategoryServicePort categoryServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;

    @Override
    public DishResponseDto createDish(Long restaurantId, Long ownerId, DishRequestDto dishRequestDto) {

        DishData dishData = dishRequestMapper.toDishData(dishRequestDto);

        Category category = categoryServicePort.getCategoryByName(dishData.getCategoryName());

        Dish dish = Dish.builder()
                .name(dishData.getName())
                .price(dishData.getPrice())
                .description(dishData.getDescription())
                .imageUrl(dishData.getImageUrl())
                .category(category)
                .restaurantId(restaurantId)
                .build();

        return dishResponseMapper.toDishResponse(dishServicePort.save(ownerId, dish));
    }
}
