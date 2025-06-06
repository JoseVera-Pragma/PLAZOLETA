package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.api.ICategoryServicePort;
import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryAlreadyExistsException;
import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryInUseException;
import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.spi.ICategoryPersistencePort;

import java.util.List;
import java.util.Optional;

public class CategoryUseCase implements ICategoryServicePort {

    private final ICategoryPersistencePort categoryPersistencePort;

    public CategoryUseCase(ICategoryPersistencePort categoryPersistencePort) {
        this.categoryPersistencePort = categoryPersistencePort;
    }

    @Override
    public Category saveCategory(Category category) {
        if (categoryPersistencePort.findCategoryByName(category.getName()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name '" + category.getName() + "' already exists");
        }
        return categoryPersistencePort.saveCategory(category);
    }

    @Override
    public Category updateCategory(Long id, Category updatedCategory) {
        categoryPersistencePort.findCategoryById(id).orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));

        Optional<Category> oldCategory = categoryPersistencePort.findCategoryByName(updatedCategory.getName());
        if (oldCategory.isPresent() && !oldCategory.get().getId().equals(id)) {
            throw new CategoryAlreadyExistsException("Category whit name " + oldCategory.get().getName() + " already exists");
        }


        Category newCategory = new Category(id, updatedCategory.getName(), updatedCategory.getDescription());

        return categoryPersistencePort.saveCategory(newCategory);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryPersistencePort.findCategoryById(id).orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryPersistencePort.findCategoryByName(name).orElseThrow(() -> new CategoryNotFoundException("Category with name " + name + " not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryPersistencePort.findAllCategories();
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryPersistencePort.findCategoryById(id).orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));
        if (categoryPersistencePort.existsDishWithCategoryId(id)) {
            throw new CategoryInUseException("Category with ID " + id + " is currently in use by one or more dishes");
        }
        categoryPersistencePort.deleteCategory(id);
    }
}
