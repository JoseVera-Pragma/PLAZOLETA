package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.spi.IDishPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {
    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    @Override
    public Dish saveDish(Dish dish) {
        return dishEntityMapper.toModel(dishRepository.save(dishEntityMapper.toEntity(dish)));
    }

    @Override
    public void updateDish(Dish dish) {
        dishRepository.save(dishEntityMapper.toEntity(dish));
    }

    @Override
    public Optional<Dish> findDishById(Long id) {
        return dishRepository.findById(id)
                .map(dishEntityMapper::toModel);
    }

    @Override
    public Page<Dish> findAllDishesByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, int pageIndex, int elementsPerPage) {
        Pageable pageable = PageRequest.of(pageIndex, elementsPerPage);

        org.springframework.data.domain.Page<DishEntity> dishPage =
                dishRepository.findAllByRestaurantIdAndCategoryId(restaurantId, categoryId, pageable);

        List<Dish> dishes = dishPage.getContent().stream()
                .map(dishEntityMapper::toModel)
                .toList();

        return new Page<>(
                dishes,
                dishPage.getNumber(),
                dishPage.getSize(),
                dishPage.getTotalElements()
        );
    }

    @Override
    public List<Dish> findDishesByRestaurantId(Long restaurantId) {
        return dishRepository.findByRestaurantId(restaurantId).stream().map(dishEntityMapper::toModel).toList();
    }
}
