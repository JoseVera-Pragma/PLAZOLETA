package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishData;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.ICategoryServicePort;
import com.plazoleta.plazoleta_microservice.domain.api.IDishServicePort;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandlerImpl implements IDishHandler {
    private final IDishServicePort dishServicePort;
    private final ICategoryServicePort categoryServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;
    private final AuthenticatedUserHandlerImpl authenticatedUserHandler;

    @Override
    public DishResponseDto createDish(Long restaurantId, DishRequestDto dishRequestDto) {

        DishData dishData = dishRequestMapper.toDishData(dishRequestDto);

        Category category = categoryServicePort.getCategoryByName(dishData.getCategoryName());

        Long ownerId = authenticatedUserHandler.getCurrentUserId();

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

    @Override
    public void updateDish(Long dishId, DishUpdateRequestDto dishUpdateRequestDto) {
        Dish existingDish = dishServicePort.getById(dishId);
        if (existingDish == null) {
            throw new DishNotFoundException("Dish with ID " + dishId + " not found");
        }

        Long ownerId = authenticatedUserHandler.getCurrentUserId();

        existingDish = Dish.builder()
                .id(existingDish.getId())
                .name(existingDish.getName())
                .description(dishUpdateRequestDto.getDescription())
                .price(dishUpdateRequestDto.getPrice())
                .restaurantId(existingDish.getRestaurantId())
                .imageUrl(existingDish.getImageUrl())
                .category(existingDish.getCategory())
                .build();

        dishServicePort.update(ownerId, existingDish);
    }

    @Override
    public void changeDishStatus(Long dishId, boolean activate) {
        Long userId = authenticatedUserHandler.getCurrentUserId();
        dishServicePort.changeDishStatus(userId, dishId, activate);
    }

    @Override
    public Page<DishResponseDto> getDishesByRestaurantAndCategory(Long restaurantId, Long categoryId, int page, int size) {
        categoryServicePort.getCategoryById(categoryId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Dish> dishPage = dishServicePort.findAllByRestaurantIdAndCategoryId(restaurantId, categoryId, pageable);

        List<DishResponseDto> dtos = dishResponseMapper.toDishResponseList(dishPage.getContent());

        return new PageImpl<>(dtos, pageable, dishPage.getTotalElements());
    }
}
