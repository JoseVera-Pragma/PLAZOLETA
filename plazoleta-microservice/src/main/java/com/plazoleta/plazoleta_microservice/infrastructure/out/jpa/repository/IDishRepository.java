package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository;

import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDishRepository extends JpaRepository<DishEntity, Long> {

    List<DishEntity> findByCategoryId(Long categoryId);

    boolean existsByCategoryId(Long categoryId);

    Page<DishEntity> findAllByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, Pageable pageable);
}
