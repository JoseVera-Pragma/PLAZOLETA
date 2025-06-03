package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.CategoryEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
        Category category = new Category(1L, "test", "test");

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

    @Test
    void testFindAllByRestaurantIdAndCategoryId() {
        Long restaurantId = 1L;
        Long categoryId = 2L;
        Pageable pageable = PageRequest.of(0, 2);

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(restaurantId);

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(categoryId);
        categoryEntity.setName("Comida Rápida");
        categoryEntity.setDescription("Comida Rápida");

        DishEntity entity1 = new DishEntity();
        entity1.setId(10L);
        entity1.setName("Hamburguesa");
        entity1.setPrice(15000.0);
        entity1.setDescription("Clásica con queso");
        entity1.setImageUrl("http://img.com/hamburguesa.jpg");
        entity1.setActive(true);
        entity1.setCategory(categoryEntity);
        entity1.setRestaurant(restaurantEntity);

        DishEntity entity2 = new DishEntity();
        entity2.setId(11L);
        entity2.setName("Perro Caliente");
        entity2.setPrice(12000.0);
        entity2.setDescription("Con tocineta");
        entity2.setImageUrl("http://img.com/perro.jpg");
        entity2.setActive(true);
        entity2.setCategory(categoryEntity);
        entity2.setRestaurant(restaurantEntity);

        List<DishEntity> entityList = List.of(entity1, entity2);
        Page<DishEntity> entityPage = new PageImpl<>(entityList, pageable, entityList.size());

        Dish dish1 = Dish.builder()
                .id(10L)
                .name("Hamburguesa")
                .price(15000.0)
                .description("Clásica con queso")
                .imageUrl("http://img.com/hamburguesa.jpg")
                .category(new Category(categoryId, "Comida Rápida", "Comida Rápida"))
                .restaurantId(restaurantId)
                .active(true)
                .build();

        Dish dish2 = Dish.builder()
                .id(11L)
                .name("Perro Caliente")
                .price(12000.0)
                .description("Con tocineta")
                .imageUrl("http://img.com/perro.jpg")
                .category(new Category(categoryId, "Comida Rápida", "Comida Rápida"))
                .restaurantId(restaurantId)
                .active(true)
                .build();

        when(dishRepository.findAllByRestaurantIdAndCategoryId(restaurantId, categoryId, pageable))
                .thenReturn(entityPage);

        when(dishEntityMapper.toModel(entity1)).thenReturn(dish1);
        when(dishEntityMapper.toModel(entity2)).thenReturn(dish2);

        Page<Dish> result = dishJpaAdapter.findAllByRestaurantIdAndCategoryId(restaurantId, categoryId, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Hamburguesa", result.getContent().get(0).getName());
        assertEquals("Perro Caliente", result.getContent().get(1).getName());

        verify(dishRepository).findAllByRestaurantIdAndCategoryId(restaurantId, categoryId, pageable);
        verify(dishEntityMapper).toModel(entity1);
        verify(dishEntityMapper).toModel(entity2);
    }

    @Test
    void getDishesByRestaurantId_ShouldReturnMappedDishes() {
        Long restaurantId = 1L;

        DishEntity dishEntity1 = new DishEntity();
        DishEntity dishEntity2 = new DishEntity();

        List<DishEntity> entityList = List.of(dishEntity1, dishEntity2);

        Dish dish1 = Dish.builder()
                .id(10L)
                .name("Hamburguesa")
                .price(15000.0)
                .description("Clásica con queso")
                .imageUrl("http://img.com/hamburguesa.jpg")
                .category(new Category(2L, "Comida Rápida", "Comida Rápida"))
                .restaurantId(restaurantId)
                .active(true)
                .build();

        Dish dish2 = Dish.builder()
                .id(11L)
                .name("Perro Caliente")
                .price(12000.0)
                .description("Con tocineta")
                .imageUrl("http://img.com/perro.jpg")
                .category(new Category(2L, "Comida Rápida", "Comida Rápida"))
                .restaurantId(restaurantId)
                .active(true)
                .build();

        when(dishRepository.findByRestaurantId(restaurantId)).thenReturn(entityList);
        when(dishEntityMapper.toModel(dishEntity1)).thenReturn(dish1);
        when(dishEntityMapper.toModel(dishEntity2)).thenReturn(dish2);

        List<Dish> result = dishJpaAdapter.getDishesByRestaurantId(restaurantId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dish1, result.get(0));
        assertEquals(dish2, result.get(1));

        verify(dishRepository).findByRestaurantId(restaurantId);
        verify(dishEntityMapper).toModel(dishEntity1);
        verify(dishEntityMapper).toModel(dishEntity2);
    }
}