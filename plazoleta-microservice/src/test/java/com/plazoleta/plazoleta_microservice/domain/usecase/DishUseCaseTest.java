package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.UnauthorizedOwnerException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.RestaurantNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.spi.ICategoryPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IDishPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class DishUseCaseTest {

    private ICategoryPersistencePort categoryPersistencePort;
    private IDishPersistencePort dishPersistencePort;
    private IRestaurantPersistencePort restaurantPersistencePort;

    private DishUseCase dishUseCase;

    @BeforeEach
    void setUp() {
        categoryPersistencePort = mock(ICategoryPersistencePort.class);
        dishPersistencePort = mock(IDishPersistencePort.class);
        restaurantPersistencePort = mock(IRestaurantPersistencePort.class);

        dishUseCase = new DishUseCase(categoryPersistencePort, dishPersistencePort, restaurantPersistencePort);
    }

    @Test
    void save_shouldThrowRestaurantNotFoundException_whenRestaurantIsNull() {
        Dish dish = Dish.builder()
                .name("Test")
                .description("Description")
                .price(0.1)
                .imageUrl("gfh")
                .restaurantId(1L)
                .category(new Category(null, "Postre", "desc"))
                .build();

        when(restaurantPersistencePort.getById(1L)).thenReturn(null);

        RestaurantNotFoundException ex = assertThrows(RestaurantNotFoundException.class, () -> {
            dishUseCase.save(10L, dish);
        });

        assertEquals("Restaurant with ID 1 not found", ex.getMessage());
        verify(restaurantPersistencePort).getById(1L);
        verifyNoMoreInteractions(categoryPersistencePort, dishPersistencePort);
    }

    @Test
    void save_shouldThrowUnauthorizedOwnerException_whenOwnerIsNotAuthorized() {
        Dish dish = Dish.builder()
                .name("Test")
                .description("Description")
                .price(0.1)
                .imageUrl("gfh")
                .restaurantId(1L)
                .category(new Category(null, "Postre", "desc"))
                .build();

        Restaurant restaurant = new Restaurant.Builder()
                .id(1L)
                .name("My Restaurant")
                .nit("123456789")
                .address("123 Main St")
                .phoneNumber("5551234")
                .urlLogo("http://logo.url")
                .idOwner(20L)
                .build();
        when(restaurantPersistencePort.getById(1L)).thenReturn(restaurant);

        UnauthorizedOwnerException ex = assertThrows(UnauthorizedOwnerException.class, () -> {
            dishUseCase.save(10L, dish);
        });

        assertEquals("Owner ID 10 is not authorized to create dishes for restaurant with ID 1", ex.getMessage());
        verify(restaurantPersistencePort).getById(1L);
        verifyNoMoreInteractions(categoryPersistencePort, dishPersistencePort);
    }

    @Test
    void save_shouldThrowCategoryNotFoundException_whenCategoryNotFound() {
        Dish dish = Dish.builder()
                .name("Test")
                .description("Description")
                .price(0.1)
                .imageUrl("gfh")
                .restaurantId(1L)
                .category(new Category(null, "Postre", "desc"))
                .build();

        Restaurant restaurant = new Restaurant.Builder()
                .id(1L)
                .name("My Restaurant")
                .nit("123456789")
                .address("123 Main St")
                .phoneNumber("5551234")
                .urlLogo("http://logo.url")
                .idOwner(10L)
                .build();

        when(restaurantPersistencePort.getById(1L)).thenReturn(restaurant);
        when(categoryPersistencePort.findByName("Postre")).thenReturn(Optional.empty());

        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> {
            dishUseCase.save(10L, dish);
        });

        assertEquals("Category with name 'Postre' not found", ex.getMessage());
        verify(restaurantPersistencePort).getById(1L);
        verify(categoryPersistencePort).findByName("Postre");
        verifyNoMoreInteractions(dishPersistencePort);
    }

    @Test
    void save_shouldReturnSavedDish_whenValidData() {
        Category category = new Category(2L, "Postre", "desc");
        Dish dish = Dish.builder()
                .id(5L)
                .name("Tarta")
                .price(15.5)
                .description("Deliciosa tarta")
                .imageUrl("http://image.url/tarta.jpg")
                .restaurantId(1L)
                .category(new Category(null, "Postre", "desc"))
                .build();

        Restaurant restaurant = new Restaurant.Builder()
                .id(1L)
                .name("My Restaurant")
                .nit("123456789")
                .address("123 Main St")
                .phoneNumber("5551234")
                .urlLogo("http://logo.url")
                .idOwner(10L)
                .build();

        when(restaurantPersistencePort.getById(1L)).thenReturn(restaurant);
        when(categoryPersistencePort.findByName("Postre")).thenReturn(Optional.of(category));
        when(dishPersistencePort.saveDish(any(Dish.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Dish savedDish = dishUseCase.save(10L, dish);

        assertNotNull(savedDish);
        assertEquals(dish.getName(), savedDish.getName());
        assertEquals(dish.getPrice(), savedDish.getPrice());
        assertEquals(category, savedDish.getCategory());
        assertEquals(dish.getRestaurantId(), savedDish.getRestaurantId());
        assertEquals(dish.getImageUrl(), savedDish.getImageUrl());

        verify(restaurantPersistencePort).getById(1L);
        verify(categoryPersistencePort).findByName("Postre");
        verify(dishPersistencePort).saveDish(any(Dish.class));
    }

    @Test
    void testGetById_shouldReturnDishIfExists() {
        Long dishId = 1L;
        Dish expectedDish = Dish.builder()
                .id(dishId)
                .name("Pasta")
                .description("Delicious pasta")
                .price(12.5)
                .restaurantId(100L)
                .imageUrl("image.jpg")
                .category(new Category(1L, "Main", "Main course"))
                .build();

        when(dishPersistencePort.getById(dishId)).thenReturn(expectedDish);

        Dish result = dishUseCase.getById(dishId);


        assertNotNull(result);
        assertEquals(expectedDish, result);
        verify(dishPersistencePort).getById(dishId);
    }

    @Test
    void testGetById_shouldReturnNullIfDishDoesNotExist() {
        Long dishId = 2L;
        when(dishPersistencePort.getById(dishId)).thenReturn(null);

        Dish result = dishUseCase.getById(dishId);

        assertNull(result);
        verify(dishPersistencePort).getById(dishId);
    }


    @Test
    void testUpdateDish_shouldUpdateOnlyDescriptionAndPrice() {
        Long ownerId = 10L;

        Dish existingDish = Dish.builder()
                .id(1L)
                .name("Pasta")
                .description("Old description")
                .price(10.0)
                .restaurantId(100L)
                .imageUrl("image.jpg")
                .category(new Category(1L, "Main","test"))
                .build();

        Dish updatedInput = Dish.builder()
                .id(1L)
                .name("Pasta")
                .description("New delicious pasta")
                .price(12.5)
                .restaurantId(100L)
                .imageUrl("image.jpg")
                .category(new Category(1L, "Main","test"))
                .build();

        Restaurant restaurant = new Restaurant.Builder()
                .id(100L)
                .name("My Restaurant")
                .nit("123456789")
                .address("123 Main St")
                .phoneNumber("5551234")
                .urlLogo("http://logo.url")
                .idOwner(10L)
                .build();

        when(dishPersistencePort.getById(1L)).thenReturn(existingDish);
        when(restaurantPersistencePort.getById(100L)).thenReturn(restaurant);

        dishUseCase.update(ownerId, updatedInput);

        verify(dishPersistencePort).updateDish(
                argThat(updated ->
                        updated.getId().equals(1L) &&
                                updated.getName().equals("Pasta") &&
                                updated.getDescription().equals("New delicious pasta") &&
                                updated.getPrice() == 12.5 &&
                                updated.getRestaurantId().equals(100L) &&
                                updated.getImageUrl().equals("image.jpg") &&
                                updated.getCategory().getName().equals("Main"))
        );
    }

    @Test
    void testUpdateDish_shouldThrowExceptionIfNotFound() {
        Long ownerId = 10L;

        Dish updatedInput = Dish.builder()
                .id(99L)
                .name("Pasta")
                .description("Old description")
                .price(10.0)
                .restaurantId(100L)
                .imageUrl("image.jpg")
                .category(new Category(1L, "Main","test"))
                .build();

        when(dishPersistencePort.getById(99L)).thenReturn(null);

        assertThrows(DishNotFoundException.class, () -> {
            dishUseCase.update(ownerId, updatedInput);
        });
    }

    @Test
    void testUpdate_shouldThrowExceptionIfRestaurantNotFound() {
        Long ownerId = 10L;

        Dish updatedInput = Dish.builder()
                .id(1L)
                .name("Pasta")
                .description("New description")
                .price(12.0)
                .restaurantId(100L)
                .imageUrl("image.jpg")
                .category(new Category(1L, "Main","test"))
                .build();

        Dish existingDish = Dish.builder()
                .id(1L)
                .name("Pasta")
                .description("Old description")
                .price(10.0)
                .restaurantId(100L)
                .imageUrl("image.jpg")
                .category(new Category(1L, "Main","test"))
                .build();

        when(dishPersistencePort.getById(1L)).thenReturn(existingDish);
        when(restaurantPersistencePort.getById(100L)).thenReturn(null);

        assertThrows(RestaurantNotFoundException.class, () -> {
            dishUseCase.update(ownerId, updatedInput);
        });
    }

    @Test
    void testUpdate_shouldThrowExceptionIfOwnerNotAuthorized() {
        Long ownerId = 10L;
        Long otherOwnerId = 20L;

        Dish updatedInput = Dish.builder()
                .id(1L)
                .name("Pasta")
                .description("New description")
                .price(12.0)
                .restaurantId(100L)
                .imageUrl("image.jpg")
                .category(new Category(1L, "Main","test"))
                .build();

        Dish existingDish = Dish.builder()
                .id(1L)
                .name("Pasta")
                .description("Old description")
                .price(10.0)
                .restaurantId(100L)
                .imageUrl("image.jpg")
                .category(new Category(1L, "Main","test"))
                .build();

        Restaurant restaurant = new Restaurant.Builder()
                .id(100L)
                .name("My Restaurant")
                .nit("123456789")
                .address("123 Main St")
                .phoneNumber("5551234")
                .urlLogo("http://logo.url")
                .idOwner(otherOwnerId)
                .build();

        when(dishPersistencePort.getById(1L)).thenReturn(existingDish);
        when(restaurantPersistencePort.getById(100L)).thenReturn(restaurant);

        assertThrows(UnauthorizedOwnerException.class, () -> {
            dishUseCase.update(ownerId, updatedInput);
        });
    }

}