package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.CategoryEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.ICategoryEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.ICategoryRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CategoryJpaAdapterTest {

    @Mock
    private ICategoryRepository categoryRepository;

    @Mock
    private IDishRepository dishRepository;

    @Mock
    private ICategoryEntityMapper categoryEntityMapper;

    @InjectMocks
    private CategoryJpaAdapter categoryJpaAdapter;

    private Category domainCategory;
    private CategoryEntity entityCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        domainCategory = new Category(1L,"Bebidas","Líquidos");
        entityCategory = new CategoryEntity();
        entityCategory.setId(1L);
        entityCategory.setName("Bebidas");
        entityCategory.setDescription("Líquidos");
    }

    @Test
    void testSaveCategory() {
        when(categoryEntityMapper.toEntity(domainCategory)).thenReturn(entityCategory);
        when(categoryRepository.save(entityCategory)).thenReturn(entityCategory);
        when(categoryEntityMapper.toModel(entityCategory)).thenReturn(domainCategory);

        Category result = categoryJpaAdapter.saveCategory(domainCategory);

        assertEquals(domainCategory, result);
        verify(categoryRepository).save(entityCategory);
    }

    @Test
    void testFindCategoryByIdFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(entityCategory));
        when(categoryEntityMapper.toModel(entityCategory)).thenReturn(domainCategory);

        Optional<Category> result = categoryJpaAdapter.findCategoryById(1L);

        assertTrue(result.isPresent());
        assertEquals(domainCategory, result.get());
    }

    @Test
    void testFindCategoryByIdNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryJpaAdapter.findCategoryById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindCategoryByNameFound() {
        when(categoryRepository.findByName("Bebidas")).thenReturn(Optional.of(entityCategory));
        when(categoryEntityMapper.toModel(entityCategory)).thenReturn(domainCategory);

        Optional<Category> result = categoryJpaAdapter.findCategoryByName("Bebidas");

        assertTrue(result.isPresent());
        assertEquals(domainCategory, result.get());
    }

    @Test
    void testFindCategoryByNameNotFound() {
        when(categoryRepository.findByName("Bebidas")).thenReturn(Optional.empty());

        Optional<Category> result = categoryJpaAdapter.findCategoryByName("Bebidas");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllCategories() {
        List<CategoryEntity> entityList = List.of(entityCategory);
        List<Category> domainList = List.of(domainCategory);

        when(categoryRepository.findAll()).thenReturn(entityList);
        when(categoryEntityMapper.toModelList(entityList)).thenReturn(domainList);

        List<Category> result = categoryJpaAdapter.findAllCategories();

        assertEquals(1, result.size());
        assertEquals(domainCategory, result.getFirst());
    }

    @Test
    void testDeleteCategory() {
        categoryJpaAdapter.deleteCategory(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void testExistsDishWithCategoryIdTrue() {
        when(dishRepository.existsByCategoryId(1L)).thenReturn(true);
        assertTrue(categoryJpaAdapter.existsDishWithCategoryId(1L));
    }

    @Test
    void testExistsDishWithCategoryIdFalse() {
        when(dishRepository.existsByCategoryId(1L)).thenReturn(false);
        assertFalse(categoryJpaAdapter.existsDishWithCategoryId(1L));
    }
}