package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryAlreadyExistsException;
import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryInUseException;
import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.spi.ICategoryPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class CategoryUseCaseTest {

    private ICategoryPersistencePort persistencePort;
    private CategoryUseCase useCase;

    private final Category category = new Category(1L, "Postre", "Descripción postres");

    @BeforeEach
    void setUp() {
        persistencePort = mock(ICategoryPersistencePort.class);
        useCase = new CategoryUseCase(persistencePort);
    }

    @Test
    void saveCategory_whenNameExists_throwsException() {
        when(persistencePort.findByName("Postre")).thenReturn(Optional.of(category));

        CategoryAlreadyExistsException ex = assertThrows(CategoryAlreadyExistsException.class, () -> {
            useCase.saveCategory(new Category(null, "Postre", "Nueva descripción"));
        });

        assertTrue(ex.getMessage().contains("already exists"));
        verify(persistencePort).findByName("Postre");
        verify(persistencePort, never()).save(any());
    }

    @Test
    void saveCategory_successful() {
        when(persistencePort.findByName("Postre")).thenReturn(Optional.empty());
        when(persistencePort.save(any(Category.class))).thenReturn(category);

        Category saved = useCase.saveCategory(new Category(null, "Postre", "Descripción"));

        assertEquals(category, saved);
        verify(persistencePort).findByName("Postre");
        verify(persistencePort).save(any(Category.class));
    }

    @Test
    void updateCategory_whenIdNotFound_throwsException() {
        when(persistencePort.findById(1L)).thenReturn(Optional.empty());

        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> {
            useCase.updateCategory(1L, new Category(null, "Postre", "Desc"));
        });

        assertTrue(ex.getMessage().contains("not found"));
        verify(persistencePort).findById(1L);
        verify(persistencePort, never()).findByName(any());
        verify(persistencePort, never()).save(any());
    }

    @Test
    void updateCategory_nameExistsWithDifferentId_throwsException() {
        Category otherCategory = new Category(2L, "Postre", "Otra descripción");

        when(persistencePort.findById(1L)).thenReturn(Optional.of(category));
        when(persistencePort.findByName("Postre")).thenReturn(Optional.of(otherCategory));

        CategoryAlreadyExistsException ex = assertThrows(CategoryAlreadyExistsException.class, () -> {
            useCase.updateCategory(1L, new Category(null, "Postre", "Desc actualizada"));
        });

        assertTrue(ex.getMessage().contains("already exists"));
        verify(persistencePort).findById(1L);
        verify(persistencePort).findByName("Postre");
        verify(persistencePort, never()).save(any());
    }

    @Test
    void updateCategory_successful() {
        when(persistencePort.findById(1L)).thenReturn(Optional.of(category));
        when(persistencePort.findByName("Postre")).thenReturn(Optional.of(category));
        when(persistencePort.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category updated = new Category(null, "Postre", "Descripción actualizada");
        Category result = useCase.updateCategory(1L, updated);

        assertEquals(1L, result.getId());
        assertEquals("Postre", result.getName());
        assertEquals("Descripción actualizada", result.getDescription());

        verify(persistencePort).findById(1L);
        verify(persistencePort).findByName("Postre");
        verify(persistencePort).save(any(Category.class));
    }

    @Test
    void updateCategory_allCases() {
        Long idToUpdate = 1L;
        Category originalCategory = new Category(idToUpdate, "Original", "Desc");
        Category updatedCategory = new Category(null, "Updated", "Desc");

        when(persistencePort.findById(idToUpdate)).thenReturn(Optional.of(originalCategory));
        when(persistencePort.findByName("Updated")).thenReturn(Optional.empty());
        when(persistencePort.save(any())).thenReturn(updatedCategory);

        Category result = useCase.updateCategory(idToUpdate, updatedCategory);
        assertEquals(updatedCategory.getName(), result.getName());

        Category oldCategorySameId = new Category(idToUpdate, "Updated", "Desc");

        when(persistencePort.findByName("Updated")).thenReturn(Optional.of(oldCategorySameId));
        when(persistencePort.save(any())).thenReturn(updatedCategory);

        result = useCase.updateCategory(idToUpdate, updatedCategory);
        assertEquals(updatedCategory.getName(), result.getName());

        Category oldCategoryDifferentId = new Category(2L, "Updated", "Desc");

        when(persistencePort.findByName("Updated")).thenReturn(Optional.of(oldCategoryDifferentId));

        CategoryAlreadyExistsException exception = assertThrows(CategoryAlreadyExistsException.class, () -> {
            useCase.updateCategory(idToUpdate, updatedCategory);
        });
        assertEquals("Category whit name Updated already exists", exception.getMessage());
    }

    @Test
    void getCategoryById_whenExists_returnsCategory() {
        when(persistencePort.findById(1L)).thenReturn(Optional.of(category));

        Category found = useCase.getCategoryById(1L);

        assertEquals(category, found);
        verify(persistencePort).findById(1L);
    }

    @Test
    void getCategoryById_whenNotFound_throwsException() {
        when(persistencePort.findById(1L)).thenReturn(Optional.empty());

        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> {
            useCase.getCategoryById(1L);
        });

        assertTrue(ex.getMessage().contains("not found"));
        verify(persistencePort).findById(1L);
    }

    @Test
    void getCategoryByName_whenExists_returnsCategory() {
        when(persistencePort.findByName("Postre")).thenReturn(Optional.of(category));

        Category found = useCase.getCategoryByName("Postre");

        assertEquals(category, found);
        verify(persistencePort).findByName("Postre");
    }

    @Test
    void getCategoryByName_whenNotFound_throwsException() {
        when(persistencePort.findByName("Postre")).thenReturn(Optional.empty());

        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> {
            useCase.getCategoryByName("Postre");
        });

        assertTrue(ex.getMessage().contains("not found"));
        verify(persistencePort).findByName("Postre");
    }

    @Test
    void getAllCategories_returnsList() {
        List<Category> categories = List.of(category, new Category(2L, "Bebida", "Descripción bebidas"));
        when(persistencePort.findAll()).thenReturn(categories);

        List<Category> result = useCase.getAllCategories();

        assertEquals(2, result.size());
        assertTrue(result.contains(category));
        verify(persistencePort).findAll();
    }

    @Test
    void deleteCategoryById_whenNotFound_throwsException() {
        when(persistencePort.findById(1L)).thenReturn(Optional.empty());

        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> {
            useCase.deleteCategoryById(1L);
        });

        assertTrue(ex.getMessage().contains("not found"));
        verify(persistencePort).findById(1L);
        verify(persistencePort, never()).existsDishWithCategoryId(anyLong());
        verify(persistencePort, never()).delete(anyLong());
    }

    @Test
    void deleteCategoryById_whenCategoryInUse_throwsException() {
        when(persistencePort.findById(1L)).thenReturn(Optional.of(category));
        when(persistencePort.existsDishWithCategoryId(1L)).thenReturn(true);

        CategoryInUseException ex = assertThrows(CategoryInUseException.class, () -> {
            useCase.deleteCategoryById(1L);
        });

        assertTrue(ex.getMessage().contains("currently in use"));
        verify(persistencePort).findById(1L);
        verify(persistencePort).existsDishWithCategoryId(1L);
        verify(persistencePort, never()).delete(anyLong());
    }

    @Test
    void deleteCategoryById_successful() {
        when(persistencePort.findById(1L)).thenReturn(Optional.of(category));
        when(persistencePort.existsDishWithCategoryId(1L)).thenReturn(false);

        useCase.deleteCategoryById(1L);

        verify(persistencePort).findById(1L);
        verify(persistencePort).existsDishWithCategoryId(1L);
        verify(persistencePort).delete(1L);
    }
}