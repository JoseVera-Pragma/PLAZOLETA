package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishJpaAdapterTest {

    @Mock
    private IDishRepository dishRepository;

    @Mock
    private IDishEntityMapper dishEntityMapper;

    @InjectMocks
    private DishJpaAdapter dishJpaAdapter;

    @Test
    void saveDish_ShouldReturnSavedDish() {
        Category category = new Category(1L,"test","test");

        Dish dish = Dish.builder()
                .id(1L)
                .name("Pizza")
                .price(12.5)
                .description("Delicious pizza")
                .imageUrl("image-url")
                .restaurantId(1L)
                .category(category)
                .build();
        DishEntity dishEntity = new DishEntity();

        Dish savedDish = Dish.builder()
                .id(1L)
                .name("Pizza")
                .price(12.5)
                .description("Delicious pizza")
                .imageUrl("image-url")
                .restaurantId(1L)
                .category(category)
                .build();

        when(dishEntityMapper.toEntity(dish)).thenReturn(dishEntity);
        when(dishRepository.save(dishEntity)).thenReturn(dishEntity);
        when(dishEntityMapper.toModel(dishEntity)).thenReturn(savedDish);

        Dish result = dishJpaAdapter.saveDish(dish);

        assertNotNull(result);
        assertEquals(savedDish, result);

        verify(dishEntityMapper).toEntity(dish);
        verify(dishRepository).save(dishEntity);
        verify(dishEntityMapper).toModel(dishEntity);
    }
}