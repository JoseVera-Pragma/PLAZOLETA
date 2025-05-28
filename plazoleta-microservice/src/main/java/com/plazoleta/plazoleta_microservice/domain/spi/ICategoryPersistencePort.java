package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryPersistencePort {
    Category save(Category category);
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
    List<Category> findAll();
    void delete(Long id);
    boolean existsDishWithCategoryId(Long categoryId);
}
