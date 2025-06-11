package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishJpaAdapterTest {

    @Mock
    private IDishRepository dishRepository;

    @Mock
    private IDishEntityMapper dishEntityMapper;

    @InjectMocks
    private DishJpaAdapter dishJpaAdapter;

    private Dish domainDish;
    private DishEntity entityDish;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        domainDish = Dish.builder().id(1L).name("Arroz").price(1000.0).build();
        entityDish = new DishEntity();
        entityDish.setId(1L);
        entityDish.setName("Arroz");
        entityDish.setPrice(1000.0);
    }

    @Test
    void testSaveDish() {
        when(dishEntityMapper.toEntity(domainDish)).thenReturn(entityDish);
        when(dishRepository.save(entityDish)).thenReturn(entityDish);
        when(dishEntityMapper.toModel(entityDish)).thenReturn(domainDish);

        Dish result = dishJpaAdapter.saveDish(domainDish);

        assertEquals(domainDish, result);
        verify(dishRepository).save(entityDish);
    }

    @Test
    void testUpdateDish() {
        when(dishEntityMapper.toEntity(domainDish)).thenReturn(entityDish);

        dishJpaAdapter.updateDish(domainDish);

        verify(dishRepository).save(entityDish);
    }

    @Test
    void testFindDishByIdFound() {
        when(dishRepository.findById(1L)).thenReturn(Optional.of(entityDish));
        when(dishEntityMapper.toModel(entityDish)).thenReturn(domainDish);

        Optional<Dish> result = dishJpaAdapter.findDishById(1L);

        assertTrue(result.isPresent());
        assertEquals(domainDish, result.get());
    }

    @Test
    void testFindDishByIdNotFound() {
        when(dishRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Dish> result = dishJpaAdapter.findDishById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllDishesByRestaurantIdAndCategoryId() {
        int pageIndex = 0;
        int elementsPerPage = 5;
        Pageable pageable = PageRequest.of(pageIndex, elementsPerPage);

        List<DishEntity> entities = List.of(entityDish);
        PageImpl<DishEntity> springPage = new PageImpl<>(entities, pageable, entities.size());

        when(dishRepository.findAllByRestaurantIdAndCategoryId(10L, 20L, pageable)).thenReturn(springPage);
        when(dishEntityMapper.toModel(entityDish)).thenReturn(domainDish);

        Page<Dish> result = dishJpaAdapter.findAllDishesByRestaurantIdAndCategoryId(10L, 20L, pageIndex, elementsPerPage);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(domainDish, result.getContent().getFirst());
        assertEquals(pageIndex, result.getPageNumber());
        assertEquals(elementsPerPage, result.getPageSize());
        assertEquals(1, result.getTotalElements());

        verify(dishRepository).findAllByRestaurantIdAndCategoryId(10L, 20L, pageable);
        verify(dishEntityMapper).toModel(entityDish);
    }

    @Test
    void testFindDishesByRestaurantId() {
        List<DishEntity> entities = List.of(entityDish);

        when(dishRepository.findByRestaurantId(10L)).thenReturn(entities);
        when(dishEntityMapper.toModel(entityDish)).thenReturn(domainDish);

        List<Dish> result = dishJpaAdapter.findDishesByRestaurantId(10L);

        assertEquals(1, result.size());
        assertEquals(domainDish, result.getFirst());
    }
}