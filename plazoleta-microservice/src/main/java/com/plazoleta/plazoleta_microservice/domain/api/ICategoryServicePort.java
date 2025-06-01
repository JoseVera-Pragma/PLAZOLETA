package com.plazoleta.plazoleta_microservice.domain.api;

import com.plazoleta.plazoleta_microservice.domain.model.Category;

import java.util.List;

public interface ICategoryServicePort {
    Category saveCategory(Category category);
    Category updateCategory(Long id, Category updatedCategory);
    Category getCategoryById(Long id);
    Category getCategoryByName(String name);
    List<Category> getAllCategories();
    void deleteCategoryById(Long id);
}
