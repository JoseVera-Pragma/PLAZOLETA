package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishResponseMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishUpdateRequestMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IDishServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DishHandlerImplTest {

    @Mock
    private IDishServicePort dishServicePort;

    @Mock
    private IDishRequestMapper dishRequestMapper;

    @Mock
    private IDishUpdateRequestMapper dishUpdateRequestMapper;

    @Mock
    private IDishResponseMapper dishResponseMapper;

    @InjectMocks
    private DishHandlerImpl dishHandler;

    @Test
    void testCreateDish() {
        DishRequestDto requestDto = DishRequestDto.builder()
                .name("Pizza")
                .description("Delicious")
                .price(15.00)
                .restaurantId(1L)
                .build();
        Dish domainDish = Dish.builder()
                .name("Pizza")
                .description("Delicious")
                .price(15.00)
                .restaurantId(1L)
                .category(new Category(1L, "categoria", "categoria"))
                .build();

        Dish savedDish = Dish.builder()
                .id(10L)
                .name("Pizza")
                .description("Delicious")
                .price(15.00)
                .restaurantId(1L)
                .category(new Category(1L, "categoria", "categoria"))
                .build();

        DishResponseDto responseDto = DishResponseDto.builder()
                                        .id(10L)
                                        .name("Pizza")
                                        .description("Delicious")
                                        .price(15.00)
                                        .active(true)
                                        .build();

        when(dishRequestMapper.toDish(requestDto)).thenReturn(domainDish);
        when(dishServicePort.saveDish(domainDish)).thenReturn(savedDish);
        when(dishResponseMapper.toDishResponse(savedDish)).thenReturn(responseDto);

        DishResponseDto result = dishHandler.createDish(requestDto);

        assertNotNull(result);
        assertEquals("Pizza", result.getName());
        verify(dishRequestMapper).toDish(requestDto);
        verify(dishServicePort).saveDish(domainDish);
        verify(dishResponseMapper).toDishResponse(savedDish);
    }

    @Test
    void testUpdateDishPriceAndDescription() {
        Long dishId = 5L;
        DishUpdateRequestDto updateDto = new DishUpdateRequestDto("New Description", 20.0);

        Dish updatedDish = Dish.builder()
                .id(dishId)
                .price(20.00)
                .description("New Description")
                .build();

        when(dishUpdateRequestMapper.toDish(updateDto)).thenReturn(updatedDish);

        dishHandler.updateDishPriceAndDescription(dishId, updateDto);

        verify(dishUpdateRequestMapper).toDish(updateDto);
        verify(dishServicePort).updateDishPriceAndDescription(dishId, updatedDish);
    }

    @Test
    void testChangeDishStatus() {
        Long dishId = 7L;
        boolean activate = true;

        dishHandler.changeDishStatus(dishId, activate);

        verify(dishServicePort).changeDishStatus(dishId, activate);
    }

    @Test
    void testGetDishesByRestaurantAndCategory() {
        Long restaurantId = 1L;
        Long categoryId = 2L;
        int pageIndex = 0;
        int elementsPerPage = 3;

        List<Dish> dishList = List.of(
                Dish.builder().id(1L).name("Taco").build(),
                Dish.builder().id(2L).name("Burrito").build()
        );

        List<DishResponseDto> responseDtos = List.of(
                DishResponseDto.builder()
                        .id(1L)
                        .name("Taco")
                        .active(true)
                        .build(),
                DishResponseDto.builder()
                        .id(2L)
                        .name("Burrito")
                        .active(true)
                        .build()
        );

        when(dishServicePort.findAllDishesByRestaurantIdAndCategoryId(restaurantId, categoryId, pageIndex, elementsPerPage))
                .thenReturn(dishList);
        when(dishResponseMapper.toDishResponseList(dishList)).thenReturn(responseDtos);

        List<DishResponseDto> result = dishHandler.getDishesByRestaurantAndCategory(restaurantId, categoryId, pageIndex, elementsPerPage);

        assertEquals(2, result.size());
        assertEquals("Taco", result.getFirst().getName());
        verify(dishServicePort).findAllDishesByRestaurantIdAndCategoryId(restaurantId, categoryId, pageIndex, elementsPerPage);
        verify(dishResponseMapper).toDishResponseList(dishList);
    }
}