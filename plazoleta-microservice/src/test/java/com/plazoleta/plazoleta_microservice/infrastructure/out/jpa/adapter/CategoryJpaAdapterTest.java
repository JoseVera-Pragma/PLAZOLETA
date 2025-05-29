package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.CategoryEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.ICategoryEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.ICategoryRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryJpaAdapterTest {

    @Mock
    private ICategoryRepository categoryRepository;

    @Mock
    private IDishRepository dishRepository;

    @Mock
    private ICategoryEntityMapper categoryEntityMapper;

    @InjectMocks
    private CategoryJpaAdapter categoryJpaAdapter;

    @Test
    void save_ShouldReturnSavedCategory() {
        Category category = new Category(1L,"Comida","Descripcion");
        Category savedCategory = new Category(1L,"Comida","Descripcion");

        when(categoryEntityMapper.toEntity(category)).thenReturn(new CategoryEntity());
        when(categoryRepository.save(any())).thenReturn(new CategoryEntity());
        when(categoryEntityMapper.toModel((CategoryEntity) any())).thenReturn(savedCategory);

        Category result = categoryJpaAdapter.save(category);

        assertNotNull(result);
        assertEquals(savedCategory, result);

        verify(categoryEntityMapper).toEntity(category);
        verify(categoryRepository).save(any());
        verify(categoryEntityMapper).toModel((CategoryEntity) any());
    }

    @Test
    void findById_ShouldReturnCategoryWhenFound() {
        Long id = 1L;
        CategoryEntity entity = new CategoryEntity();
        Category category = new Category(1L,"Comida","Descripcion");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(entity));
        when(categoryEntityMapper.toModel(entity)).thenReturn(category);

        Optional<Category> result = categoryJpaAdapter.findById(id);

        assertTrue(result.isPresent());
        assertEquals(category, result.get());

        verify(categoryRepository).findById(id);
        verify(categoryEntityMapper).toModel(entity);
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotFound() {
        Long id = 1L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Category> result = categoryJpaAdapter.findById(id);

        assertTrue(result.isEmpty());
        verify(categoryRepository).findById(id);
        verifyNoInteractions(categoryEntityMapper);
    }

    @Test
    void findByName_ShouldReturnCategoryWhenFound() {
        String name = "Italian";
        CategoryEntity entity = new CategoryEntity();
        Category category = new Category(1L,"Comida","Descripcion");

        when(categoryRepository.findByName(name)).thenReturn(Optional.of(entity));
        when(categoryEntityMapper.toModel(entity)).thenReturn(category);

        Optional<Category> result = categoryJpaAdapter.findByName(name);

        assertTrue(result.isPresent());
        assertEquals(category, result.get());

        verify(categoryRepository).findByName(name);
        verify(categoryEntityMapper).toModel(entity);
    }

    @Test
    void findByName_ShouldReturnEmptyWhenNotFound() {
        String name = "Italian";

        when(categoryRepository.findByName(name)).thenReturn(Optional.empty());

        Optional<Category> result = categoryJpaAdapter.findByName(name);

        assertTrue(result.isEmpty());
        verify(categoryRepository).findByName(name);
        verifyNoInteractions(categoryEntityMapper);
    }

    @Test
    void findAll_ShouldReturnListOfCategories() {
        List<CategoryEntity> entities = List.of(
                new CategoryEntity(),
                new CategoryEntity()
        );
        List<Category> categories = List.of(new Category(1L,"Comida","Descripcion"), new Category(1L,"Comida","Descripcion"));

        when(categoryRepository.findAll()).thenReturn(entities);
        when(categoryEntityMapper.toModelList(entities)).thenReturn(categories);

        List<Category> result = categoryJpaAdapter.findAll();

        assertEquals(categories, result);
        verify(categoryRepository).findAll();
        verify(categoryEntityMapper).toModelList(entities);
    }

    @Test
    void delete_ShouldCallRepositoryDeleteById() {
        Long id = 1L;

        doNothing().when(categoryRepository).deleteById(id);

        categoryJpaAdapter.delete(id);

        verify(categoryRepository).deleteById(id);
    }

    @Test
    void existsDishWithCategoryId_ShouldReturnTrueWhenExists() {
        Long categoryId = 1L;

        when(dishRepository.existsByCategoryId(categoryId)).thenReturn(true);

        boolean result = categoryJpaAdapter.existsDishWithCategoryId(categoryId);

        assertTrue(result);
        verify(dishRepository).existsByCategoryId(categoryId);
    }

    @Test
    void existsDishWithCategoryId_ShouldReturnFalseWhenNotExists() {
        Long categoryId = 1L;

        when(dishRepository.existsByCategoryId(categoryId)).thenReturn(false);

        boolean result = categoryJpaAdapter.existsDishWithCategoryId(categoryId);

        assertFalse(result);
        verify(dishRepository).existsByCategoryId(categoryId);
    }
}