package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishData;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IDishResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.ICategoryServicePort;
import com.plazoleta.plazoleta_microservice.domain.api.IDishServicePort;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.InvalidDishDataException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishHandlerImplTest {

    @Mock
    private IDishServicePort dishServicePort;

    @Mock
    private ICategoryServicePort categoryServicePort;

    @Mock
    private IDishRequestMapper dishRequestMapper;

    @Mock
    private IDishResponseMapper dishResponseMapper;

    @InjectMocks
    private DishHandlerImpl dishHandler;

    private DishRequestDto dishRequestDto;
    private DishData dishData;
    private Category category;
    private Dish dish;
    private Dish savedDish;
    private DishResponseDto dishResponseDto;
    private Long restaurantId;
    private Long ownerId;

    @BeforeEach
    void setUp() {
        restaurantId = 1L;
        ownerId = 1L;

        dishRequestDto = DishRequestDto.builder()
                .name("Pizza Margherita")
                .price(20.0)
                .description("Pizza con tomate, mozzarella y albahaca")
                .imageUrl("https://example.com/pizza.jpg")
                .categoryName("Pizza")
                .build();

        dishData = DishData.builder()
                .name("Pizza Margherita")
                .price(20.0)
                .description("Pizza con tomate, mozzarella y albahaca")
                .imageUrl("https://example.com/pizza.jpg")
                .categoryName("Pizza")
                .build();

        category = new Category(1L,"Pizza","Categoría de pizzas");
        dish = Dish.builder()
                .name("Pizza Margherita")
                .price(20.0)
                .description("Pizza con tomate, mozzarella y albahaca")
                .imageUrl("https://example.com/pizza.jpg")
                .category(category)
                .restaurantId(restaurantId)
                .build();

        savedDish = Dish.builder()
                .id(1L)
                .name("Pizza Margherita")
                .price(20.0)
                .description("Pizza con tomate, mozzarella y albahaca")
                .imageUrl("https://example.com/pizza.jpg")
                .category(category)
                .restaurantId(restaurantId)
                .active(true)
                .build();

        dishResponseDto = DishResponseDto.builder()
                .id(1L)
                .name("Pizza Margherita")
                .price(20.0)
                .description("Pizza con tomate, mozzarella y albahaca")
                .imageUrl("https://example.com/pizza.jpg")
                .categoryName("Pizza")
                .restaurantId(restaurantId)
                .active(true)
                .build();
    }

    @Test
    void createDish_ShouldReturnDishResponseDto_WhenValidData() {
        when(dishRequestMapper.toDishData(dishRequestDto)).thenReturn(dishData);
        when(categoryServicePort.getCategoryByName("Pizza")).thenReturn(category);
        when(dishServicePort.save(eq(ownerId), any(Dish.class))).thenReturn(savedDish);
        OngoingStubbing<DishResponseDto> dishResponseDtoOngoingStubbing = when(dishResponseMapper.toDishResponse(savedDish)).thenReturn(dishResponseDto);

        DishResponseDto result = dishHandler.createDish(restaurantId, ownerId, dishRequestDto);

        assertNotNull(result);
        assertEquals(dishResponseDto.getId(), result.getId());
        assertEquals(dishResponseDto.getName(), result.getName());
        assertEquals(dishResponseDto.getPrice(), result.getPrice());
        assertEquals(dishResponseDto.getDescription(), result.getDescription());
        assertEquals(dishResponseDto.getImageUrl(), result.getImageUrl());
        assertEquals(dishResponseDto.getCategoryName(), result.getCategoryName());
        assertEquals(dishResponseDto.getRestaurantId(), result.getRestaurantId());
        assertTrue(result.isActive());

        verify(dishRequestMapper, times(1)).toDishData(dishRequestDto);
        verify(categoryServicePort, times(1)).getCategoryByName("Pizza");
        verify(dishServicePort, times(1)).save(eq(ownerId), any(Dish.class));
        verify(dishResponseMapper, times(1)).toDishResponse(savedDish);
    }

    @Test
    void createDish_ShouldBuildDishWithCorrectProperties_WhenCalled() {
        when(dishRequestMapper.toDishData(dishRequestDto)).thenReturn(dishData);
        when(categoryServicePort.getCategoryByName("Pizza")).thenReturn(category);
        when(dishServicePort.save(eq(ownerId), any(Dish.class))).thenReturn(savedDish);
        when(dishResponseMapper.toDishResponse(savedDish)).thenReturn(dishResponseDto);

        dishHandler.createDish(restaurantId, ownerId, dishRequestDto);

        verify(dishServicePort).save(eq(ownerId), argThat(dish ->
                dish.getName().equals("Pizza Margherita") &&
                        dish.getPrice().equals(20.0) &&
                        dish.getDescription().equals("Pizza con tomate, mozzarella y albahaca") &&
                        dish.getImageUrl().equals("https://example.com/pizza.jpg") &&
                        dish.getCategory().equals(category) &&
                        dish.getRestaurantId().equals(restaurantId)
        ));
    }

    @Test
    void createDish_ShouldThrowException_WhenRequiredFieldsAreNull() {
        DishData dishDataWithNulls = DishData.builder()
                .name("Test Dish")
                .price(10.0)
                .description(null)
                .imageUrl(null)
                .categoryName("Pizza")
                .build();

        when(dishRequestMapper.toDishData(dishRequestDto)).thenReturn(dishDataWithNulls);
        when(categoryServicePort.getCategoryByName("Pizza")).thenReturn(category);

        assertThrows(InvalidDishDataException.class, () -> {
            dishHandler.createDish(restaurantId, ownerId, dishRequestDto);
        });
    }

    @Test
    void createDish_ShouldPropagateException_WhenCategoryServiceFails() {

        when(dishRequestMapper.toDishData(dishRequestDto)).thenReturn(dishData);
        when(categoryServicePort.getCategoryByName("Pizza"))
                .thenThrow(new RuntimeException("Category not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                dishHandler.createDish(restaurantId, ownerId, dishRequestDto)
        );

        assertEquals("Category not found", exception.getMessage());
        verify(dishRequestMapper, times(1)).toDishData(dishRequestDto);
        verify(categoryServicePort, times(1)).getCategoryByName("Pizza");
        verify(dishServicePort, never()).save(any(), any());
        verify(dishResponseMapper, never()).toDishResponse(any());
    }

    @Test
    void createDish_ShouldPropagateException_WhenDishServiceFails() {

        when(dishRequestMapper.toDishData(dishRequestDto)).thenReturn(dishData);
        when(categoryServicePort.getCategoryByName("Pizza")).thenReturn(category);
        when(dishServicePort.save(eq(ownerId), any(Dish.class)))
                .thenThrow(new RuntimeException("Failed to save dish"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                dishHandler.createDish(restaurantId, ownerId, dishRequestDto)
        );

        assertEquals("Failed to save dish", exception.getMessage());
        verify(dishRequestMapper, times(1)).toDishData(dishRequestDto);
        verify(categoryServicePort, times(1)).getCategoryByName("Pizza");
        verify(dishServicePort, times(1)).save(eq(ownerId), any(Dish.class));
        verify(dishResponseMapper, never()).toDishResponse(any());
    }

    @Test
    void createDish_ShouldThrowException_WhenPriceIsZero() {

        DishData dishDataWithZeroPrice = DishData.builder()
                .name("Free Sample")
                .price(0.0)
                .description("Free dish sample")
                .imageUrl("https://example.com/free.jpg")
                .categoryName("Pizza")
                .build();

        when(dishRequestMapper.toDishData(dishRequestDto)).thenReturn(dishDataWithZeroPrice);
        when(categoryServicePort.getCategoryByName("Pizza")).thenReturn(category);

        InvalidDishDataException thrown = assertThrows(InvalidDishDataException.class, () -> {
            dishHandler.createDish(restaurantId, ownerId, dishRequestDto);
        });

        assertEquals("The price of the dish must be a positive integer greater than 0.", thrown.getMessage());
    }

    @Test
    void createDish_ShouldUseCorrectIds_WhenCalled() {

        Long specificRestaurantId = 999L;
        Long specificOwnerId = 888L;

        when(dishRequestMapper.toDishData(dishRequestDto)).thenReturn(dishData);
        when(categoryServicePort.getCategoryByName("Pizza")).thenReturn(category);
        when(dishServicePort.save(eq(specificOwnerId), any(Dish.class))).thenReturn(savedDish);
        when(dishResponseMapper.toDishResponse(savedDish)).thenReturn(dishResponseDto);

        dishHandler.createDish(specificRestaurantId, specificOwnerId, dishRequestDto);

        verify(dishServicePort).save(eq(specificOwnerId), argThat(dish ->
                dish.getRestaurantId().equals(specificRestaurantId)
        ));
    }

    @Test
    void createDish_ShouldCallDependenciesInCorrectOrder_WhenCalled() {

        when(dishRequestMapper.toDishData(dishRequestDto)).thenReturn(dishData);
        when(categoryServicePort.getCategoryByName("Pizza")).thenReturn(category);
        when(dishServicePort.save(eq(ownerId), any(Dish.class))).thenReturn(savedDish);
        when(dishResponseMapper.toDishResponse(savedDish)).thenReturn(dishResponseDto);

        dishHandler.createDish(restaurantId, ownerId, dishRequestDto);

        var inOrder = inOrder(dishRequestMapper, categoryServicePort, dishServicePort, dishResponseMapper);
        inOrder.verify(dishRequestMapper).toDishData(dishRequestDto);
        inOrder.verify(categoryServicePort).getCategoryByName("Pizza");
        inOrder.verify(dishServicePort).save(eq(ownerId), any(Dish.class));
        inOrder.verify(dishResponseMapper).toDishResponse(savedDish);
    }

    @Test
    void testUpdateDish_shouldUpdateDishSuccessfully() {
        Long ownerId = 1L;
        Long dishId = 10L;

        DishUpdateRequestDto dto = new DishUpdateRequestDto();
        dto.setDescription("Updated description");
        dto.setPrice(15.0);

        Dish existingDish = Dish.builder()
                .id(dishId)
                .name("Pasta")
                .description("Old description")
                .price(12.0)
                .restaurantId(100L)
                .imageUrl("image.jpg")
                .category(new Category(1L, "Main", "Main"))
                .build();

        when(dishServicePort.getById(dishId)).thenReturn(existingDish);

        dishHandler.updateDish(ownerId, dishId, dto);

        verify(dishServicePort).update(eq(ownerId), argThat(updated ->
                updated.getId().equals(dishId) &&
                        updated.getName().equals("Pasta") &&
                        updated.getDescription().equals("Updated description") &&
                        updated.getPrice() == 15.0 &&
                        updated.getRestaurantId().equals(100L) &&
                        updated.getImageUrl().equals("image.jpg") &&
                        updated.getCategory().getName().equals("Main")
        ));
    }

    @Test
    void testUpdateDish_shouldThrowExceptionWhenDishNotFound() {
        Long ownerId = 1L;
        Long dishId = 99L;

        DishUpdateRequestDto dto = new DishUpdateRequestDto();
        dto.setDescription("New description");
        dto.setPrice(20.0);

        when(dishServicePort.getById(dishId)).thenReturn(null);

        assertThrows(DishNotFoundException.class, () -> {
            dishHandler.updateDish(ownerId, dishId, dto);
        });

        verify(dishServicePort, never()).update(any(), any());
    }

}