package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
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

import java.util.Optional;

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

    @Test
    void testUpdateDish_shouldSaveDishEntity() {
        Dish dish = Dish.builder()
                .id(1L)
                .name("Updated Dish")
                .description("Updated Description")
                .price(14.5)
                .restaurantId(100L)
                .imageUrl("image.jpg")
                .category(new Category(1L, "Main", "Main"))
                .build();

        DishEntity dishEntity = new DishEntity();
        when(dishEntityMapper.toEntity(dish)).thenReturn(dishEntity);

        dishJpaAdapter.updateDish(dish);

        verify(dishEntityMapper).toEntity(dish);
        verify(dishRepository).save(dishEntity);
    }

    @Test
    void testGetById_shouldReturnDishIfExists() {
        Long id = 1L;
        DishEntity entity = new DishEntity();
        Dish expectedDish = Dish.builder()
                .id(id)
                .name("Dish Name")
                .description("Some description")
                .price(12.0)
                .restaurantId(100L)
                .imageUrl("img.jpg")
                .category(new Category(1L, "Main", "Main"))
                .build();

        when(dishRepository.findById(id)).thenReturn(Optional.of(entity));
        when(dishEntityMapper.toModel(entity)).thenReturn(expectedDish);

        Dish result = dishJpaAdapter.getById(id);

        assertNotNull(result);
        assertEquals(expectedDish, result);
        verify(dishRepository).findById(id);
        verify(dishEntityMapper).toModel(entity);
    }

    @Test
    void testGetById_shouldThrowExceptionWhenNotFound() {
        Long id = 2L;
        when(dishRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DishNotFoundException.class, () -> {
            dishJpaAdapter.getById(id);
        });

        verify(dishRepository).findById(id);
        verify(dishEntityMapper, never()).toModel(any(DishEntity.class));
    }


}