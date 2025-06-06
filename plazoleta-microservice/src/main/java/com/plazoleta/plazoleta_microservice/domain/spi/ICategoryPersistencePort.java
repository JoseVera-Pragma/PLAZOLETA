package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryPersistencePort {
    Category saveCategory(Category category);

    Optional<Category> findCategoryById(Long id);

    Optional<Category> findCategoryByName(String name);

    List<Category> findAllCategories();

    void deleteCategory(Long id);

    boolean existsDishWithCategoryId(Long categoryId);
}
