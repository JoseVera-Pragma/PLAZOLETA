package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryAlreadyExistsException;
import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryInUseException;
import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.spi.ICategoryPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryUseCaseTest {

    @Mock
    private ICategoryPersistencePort persistencePort;

    @InjectMocks
    private CategoryUseCase useCase;

    private final Category category = new Category(1L, "Postre", "Descripción postres");

    @BeforeEach
    void setUp() {
        useCase = new CategoryUseCase(persistencePort);
    }

    @Test
    void saveCategory_whenNameExists_throwsException() {
        when(persistencePort.findCategoryByName("Postre")).thenReturn(Optional.of(category));

        CategoryAlreadyExistsException ex = assertThrows(CategoryAlreadyExistsException.class, () -> {
            useCase.saveCategory(new Category(null, "Postre", "Nueva descripción"));
        });

        assertTrue(ex.getMessage().contains("already exists"));
        verify(persistencePort).findCategoryByName("Postre");
        verify(persistencePort, never()).saveCategory(any());
    }

    @Test
    void saveCategory_successful() {
        when(persistencePort.findCategoryByName("Postre")).thenReturn(Optional.empty());
        when(persistencePort.saveCategory(any(Category.class))).thenReturn(category);

        Category saved = useCase.saveCategory(new Category(null, "Postre", "Descripción"));

        assertEquals(category, saved);
        verify(persistencePort).findCategoryByName("Postre");
        verify(persistencePort).saveCategory(any(Category.class));
    }

    @Test
    void updateCategory_whenIdNotFound_throwsException() {
        when(persistencePort.findCategoryById(1L)).thenReturn(Optional.empty());

        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> {
            useCase.updateCategory(1L, new Category(null, "Postre", "Desc"));
        });

        assertTrue(ex.getMessage().contains("not found"));
        verify(persistencePort).findCategoryById(1L);
        verify(persistencePort, never()).findCategoryByName(any());
        verify(persistencePort, never()).saveCategory(any());
    }

    @Test
    void updateCategory_nameExistsWithDifferentId_throwsException() {
        Category otherCategory = new Category(2L, "Postre", "Otra descripción");

        when(persistencePort.findCategoryById(1L)).thenReturn(Optional.of(category));
        when(persistencePort.findCategoryByName("Postre")).thenReturn(Optional.of(otherCategory));

        CategoryAlreadyExistsException ex = assertThrows(CategoryAlreadyExistsException.class, () -> {
            useCase.updateCategory(1L, new Category(null, "Postre", "Desc actualizada"));
        });

        assertTrue(ex.getMessage().contains("already exists"));
        verify(persistencePort).findCategoryById(1L);
        verify(persistencePort).findCategoryByName("Postre");
        verify(persistencePort, never()).saveCategory(any());
    }

    @Test
    void updateCategory_successful() {
        when(persistencePort.findCategoryById(1L)).thenReturn(Optional.of(category));
        when(persistencePort.findCategoryByName("Postre")).thenReturn(Optional.of(category));
        when(persistencePort.saveCategory(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category updated = new Category(null, "Postre", "Descripción actualizada");
        Category result = useCase.updateCategory(1L, updated);

        assertEquals(1L, result.getId());
        assertEquals("Postre", result.getName());
        assertEquals("Descripción actualizada", result.getDescription());

        verify(persistencePort).findCategoryById(1L);
        verify(persistencePort).findCategoryByName("Postre");
        verify(persistencePort).saveCategory(any(Category.class));
    }

    @Test
    void updateCategory_allCases() {
        Long idToUpdate = 1L;
        Category originalCategory = new Category(idToUpdate, "Original", "Desc");
        Category updatedCategory = new Category(null, "Updated", "Desc");

        when(persistencePort.findCategoryById(idToUpdate)).thenReturn(Optional.of(originalCategory));
        when(persistencePort.findCategoryByName("Updated")).thenReturn(Optional.empty());
        when(persistencePort.saveCategory(any())).thenReturn(updatedCategory);

        Category result = useCase.updateCategory(idToUpdate, updatedCategory);
        assertEquals(updatedCategory.getName(), result.getName());

        Category oldCategorySameId = new Category(idToUpdate, "Updated", "Desc");

        when(persistencePort.findCategoryByName("Updated")).thenReturn(Optional.of(oldCategorySameId));
        when(persistencePort.saveCategory(any())).thenReturn(updatedCategory);

        result = useCase.updateCategory(idToUpdate, updatedCategory);
        assertEquals(updatedCategory.getName(), result.getName());

        Category oldCategoryDifferentId = new Category(2L, "Updated", "Desc");

        when(persistencePort.findCategoryByName("Updated")).thenReturn(Optional.of(oldCategoryDifferentId));

        CategoryAlreadyExistsException exception = assertThrows(CategoryAlreadyExistsException.class, () -> {
            useCase.updateCategory(idToUpdate, updatedCategory);
        });
        assertEquals("Category whit name Updated already exists", exception.getMessage());
    }

    @Test
    void getCategoryById_whenExists_returnsCategory() {
        when(persistencePort.findCategoryById(1L)).thenReturn(Optional.of(category));

        Category found = useCase.getCategoryById(1L);

        assertEquals(category, found);
        verify(persistencePort).findCategoryById(1L);
    }

    @Test
    void getCategoryById_whenNotFound_throwsException() {
        when(persistencePort.findCategoryById(1L)).thenReturn(Optional.empty());

        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> {
            useCase.getCategoryById(1L);
        });

        assertTrue(ex.getMessage().contains("not found"));
        verify(persistencePort).findCategoryById(1L);
    }

    @Test
    void getCategoryByName_whenExists_returnsCategory() {
        when(persistencePort.findCategoryByName("Postre")).thenReturn(Optional.of(category));

        Category found = useCase.getCategoryByName("Postre");

        assertEquals(category, found);
        verify(persistencePort).findCategoryByName("Postre");
    }

    @Test
    void getCategoryByName_whenNotFound_throwsException() {
        when(persistencePort.findCategoryByName("Postre")).thenReturn(Optional.empty());

        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> {
            useCase.getCategoryByName("Postre");
        });

        assertTrue(ex.getMessage().contains("not found"));
        verify(persistencePort).findCategoryByName("Postre");
    }

    @Test
    void getAllCategories_returnsList() {
        List<Category> categories = List.of(category, new Category(2L, "Bebida", "Descripción bebidas"));
        when(persistencePort.findAllCategories()).thenReturn(categories);

        List<Category> result = useCase.getAllCategories();

        assertEquals(2, result.size());
        assertTrue(result.contains(category));
        verify(persistencePort).findAllCategories();
    }

    @Test
    void deleteCategoryById_whenNotFound_throwsException() {
        when(persistencePort.findCategoryById(1L)).thenReturn(Optional.empty());

        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> {
            useCase.deleteCategoryById(1L);
        });

        assertTrue(ex.getMessage().contains("not found"));
        verify(persistencePort).findCategoryById(1L);
        verify(persistencePort, never()).existsDishWithCategoryId(anyLong());
        verify(persistencePort, never()).deleteCategory(anyLong());
    }

    @Test
    void deleteCategoryById_whenCategoryInUse_throwsException() {
        when(persistencePort.findCategoryById(1L)).thenReturn(Optional.of(category));
        when(persistencePort.existsDishWithCategoryId(1L)).thenReturn(true);

        CategoryInUseException ex = assertThrows(CategoryInUseException.class, () -> {
            useCase.deleteCategoryById(1L);
        });

        assertTrue(ex.getMessage().contains("currently in use"));
        verify(persistencePort).findCategoryById(1L);
        verify(persistencePort).existsDishWithCategoryId(1L);
        verify(persistencePort, never()).deleteCategory(anyLong());
    }

    @Test
    void deleteCategoryById_successful() {
        when(persistencePort.findCategoryById(1L)).thenReturn(Optional.of(category));
        when(persistencePort.existsDishWithCategoryId(1L)).thenReturn(false);

        useCase.deleteCategoryById(1L);

        verify(persistencePort).findCategoryById(1L);
        verify(persistencePort).existsDishWithCategoryId(1L);
        verify(persistencePort).deleteCategory(1L);
    }
}