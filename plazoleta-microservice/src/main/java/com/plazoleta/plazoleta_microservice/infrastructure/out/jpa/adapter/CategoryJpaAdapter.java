package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.spi.ICategoryPersistencePort;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.ICategoryEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.ICategoryRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryJpaAdapter implements ICategoryPersistencePort {

    private final ICategoryRepository categoryRepository;
    private final IDishRepository dishRepository;
    private final ICategoryEntityMapper categoryEntityMapper;

    @Override
    public Category saveCategory(Category category) {
        return categoryEntityMapper.toModel(categoryRepository.save(categoryEntityMapper.toEntity(category)));
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id).map(categoryEntityMapper::toModel);
    }

    @Override
    public Optional<Category> findCategoryByName(String name) {
        return categoryRepository.findByName(name).map(categoryEntityMapper::toModel);
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryEntityMapper.toModelList(categoryRepository.findAll());
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsDishWithCategoryId(Long categoryId) {
        return dishRepository.existsByCategoryId(categoryId);
    }
}
