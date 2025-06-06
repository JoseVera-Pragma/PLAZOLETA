package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.UnauthorizedOwnerException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.RestaurantNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.spi.IAuthenticatedUserPort;
import com.plazoleta.plazoleta_microservice.domain.spi.ICategoryPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IDishPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishUseCaseTest {

    @Mock
    private IDishPersistencePort dishPersistencePort;
    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock
    private ICategoryPersistencePort categoryPersistencePort;
    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    @InjectMocks
    private DishUseCase dishUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveDish_shouldSaveSuccessfully_whenValidOwnerAndCategory() {
        Long ownerId = 1L;
        Long restaurantId = 10L;
        String categoryName = "test";

        Restaurant restaurant = Restaurant.builder().idOwner(ownerId).id(restaurantId).build();
        Category category = new Category(1L,"test","test");

        Dish dish = Dish.builder()
                .id(10L)
                .name("Pizza")
                .price(12.5)
                .description("Delicious pizza")
                .imageUrl("http://image.url/pizza.jpg")
                .category(category)
                .active(true)
                .restaurantId(restaurantId)
                .build();


        Dish dishSaved = Dish.builder()
                .id(10L)
                .name("Pizza")
                .price(12.5)
                .description("Delicious pizza")
                .imageUrl("http://image.url/pizza.jpg")
                .category(category)
                .active(true)
                .restaurantId(restaurantId)
                .build();

        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(ownerId));
        when(categoryPersistencePort.findCategoryByName(categoryName)).thenReturn(Optional.of(category));
        when(dishPersistencePort.saveDish(any(Dish.class))).thenReturn(dishSaved);

        Dish result = dishUseCase.saveDish(dish);

        assertNotNull(result);
        assertTrue(result.isActive());
        verify(dishPersistencePort).saveDish(any(Dish.class));
    }

    @Test
    void saveDish_shouldThrowRestaurantNotFoundException_whenRestaurantNotFound() {
        Long restaurantId = 10L;
        Dish dish = Dish.builder().restaurantId(restaurantId).build();

        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> dishUseCase.saveDish(dish));
    }

    @Test
    void saveDish_shouldThrowUserNotFoundException_whenUserNotFound() {
        Long restaurantId = 10L;
        Restaurant restaurant = Restaurant.builder().idOwner(1L).id(restaurantId).build();
        Dish dish = Dish.builder().restaurantId(restaurantId).build();

        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> dishUseCase.saveDish(dish));
    }

    @Test
    void saveDish_shouldThrowUnauthorizedOwnerException_whenUserIsNotOwner() {
        Long restaurantId = 10L;
        Restaurant restaurant = Restaurant.builder().idOwner(2L).id(restaurantId).build();
        Dish dish = Dish.builder().restaurantId(restaurantId).build();

        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(1L));

        assertThrows(UnauthorizedOwnerException.class, () -> dishUseCase.saveDish(dish));
    }

    @Test
    void saveDish_shouldThrowCategoryNotFoundException_whenCategoryNotFound() {
        Long restaurantId = 10L;
        String categoryName = "Category1";
        Restaurant restaurant = Restaurant.builder().idOwner(1L).id(restaurantId).build();
        Dish dish = Dish.builder()
                .restaurantId(restaurantId)
                .category(new Category(1L,"test","test"))
                .build();

        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(1L));
        when(categoryPersistencePort.findCategoryByName(categoryName)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> dishUseCase.saveDish(dish));
    }

    @Test
    void findDishById_shouldReturnDish_whenDishExists() {
        Long dishId = 5L;
        Dish dish = Dish.builder().id(dishId).build();

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.of(dish));

        Dish result = dishUseCase.findDishById(dishId);

        assertEquals(dish, result);
    }

    @Test
    void findDishById_shouldThrowDishNotFoundException_whenDishNotFound() {
        Long dishId = 5L;

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.empty());

        assertThrows(DishNotFoundException.class, () -> dishUseCase.findDishById(dishId));
    }

    @Test
    void updateDishPriceAndDescription_shouldUpdateSuccessfully_whenAuthorized() {
        Long ownerId = 1L;
        Long dishId = 5L;
        Long restaurantId = 10L;
        Dish existingDish = Dish.builder().id(dishId).restaurantId(restaurantId).build();
        Restaurant restaurant = Restaurant.builder().idOwner(ownerId).id(restaurantId).build();
        Dish updateData = Dish.builder().price(100.0).description("Nuevo plato").build();

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.of(existingDish));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(ownerId));

        dishUseCase.updateDishPriceAndDescription(dishId, updateData);

        verify(dishPersistencePort).updateDish(any(Dish.class));
    }

    @Test
    void updateDishPriceAndDescription_shouldThrowDishNotFoundException_whenDishNotFound() {
        Long dishId = 5L;
        Dish updateData = Dish.builder().price(100.0).description("Nuevo plato").build();

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.empty());

        assertThrows(DishNotFoundException.class, () -> dishUseCase.updateDishPriceAndDescription(dishId, updateData));
    }

    @Test
    void updateDishPriceAndDescription_shouldThrowRestaurantNotFoundException_whenRestaurantNotFound() {
        Long dishId = 5L;
        Long restaurantId = 10L;
        Dish existingDish = Dish.builder().id(dishId).restaurantId(restaurantId).build();
        Dish updateData = Dish.builder().price(100.0).description("Nuevo plato").build();

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.of(existingDish));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> dishUseCase.updateDishPriceAndDescription(dishId, updateData));
    }

    @Test
    void updateDishPriceAndDescription_shouldThrowUserNotFoundException_whenUserNotFound() {
        Long ownerId = 1L;
        Long dishId = 5L;
        Long restaurantId = 10L;
        Dish existingDish = Dish.builder().id(dishId).restaurantId(restaurantId).build();
        Restaurant restaurant = Restaurant.builder().idOwner(ownerId).id(restaurantId).build();
        Dish updateData = Dish.builder().price(100.0).description("Nuevo plato").build();

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.of(existingDish));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> dishUseCase.updateDishPriceAndDescription(dishId, updateData));
    }

    @Test
    void updateDishPriceAndDescription_shouldThrowUnauthorizedOwnerException_whenUserNotAuthorized() {
        Long ownerId = 2L;
        Long dishId = 5L;
        Long restaurantId = 10L;
        Dish existingDish = Dish.builder().id(dishId).restaurantId(restaurantId).build();
        Restaurant restaurant = Restaurant.builder().idOwner(1L).id(restaurantId).build();
        Dish updateData = Dish.builder().price(100.0).description("Nuevo plato").build();

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.of(existingDish));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(ownerId));

        assertThrows(UnauthorizedOwnerException.class,
                () -> dishUseCase.updateDishPriceAndDescription(dishId, updateData));
    }

    @Test
    void changeDishStatus_shouldChangeSuccessfully_whenAuthorizedAndStatusIsFalse() {
        Long ownerId = 1L;
        Long dishId = 5L;
        Long restaurantId = 10L;
        Dish dish = Dish.builder().id(dishId).restaurantId(restaurantId).active(false).build();
        Restaurant restaurant = Restaurant.builder().idOwner(ownerId).id(restaurantId).build();

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.of(dish));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(ownerId));

        dishUseCase.changeDishStatus(dishId, true);

        verify(dishPersistencePort).updateDish(any(Dish.class));
    }

    @Test
    void changeDishStatus_shouldChangeSuccessfully_whenAuthorizedAndStatusIsTrue() {
        Long ownerId = 1L;
        Long dishId = 5L;
        Long restaurantId = 10L;
        Dish dish = Dish.builder().id(dishId).restaurantId(restaurantId).active(true).build();
        Restaurant restaurant = Restaurant.builder().idOwner(ownerId).id(restaurantId).build();

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.of(dish));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(ownerId));

        dishUseCase.changeDishStatus(dishId, false);

        verify(dishPersistencePort).updateDish(any(Dish.class));
    }

    @Test
    void changeDishStatus_shouldThrowDishNotFoundException_whenDishNotFound() {
        Long dishId = 5L;

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.empty());

        assertThrows(DishNotFoundException.class,
                () -> dishUseCase.changeDishStatus(dishId, true));
    }

    @Test
    void changeDishStatus_shouldThrowRestaurantNotFoundException_whenRestaurantNotFound() {
        Long dishId = 5L;
        Long restaurantId = 10L;
        Dish dish = Dish.builder().id(dishId).restaurantId(restaurantId).active(false).build();

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.of(dish));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class,
                () -> dishUseCase.changeDishStatus(dishId, true));
    }

    @Test
    void changeDishStatus_shouldThrowUserNotFoundException_whenUserNotFound() {
        Long ownerId = 1L;
        Long dishId = 5L;
        Long restaurantId = 10L;
        Dish dish = Dish.builder().id(dishId).restaurantId(restaurantId).active(false).build();
        Restaurant restaurant = Restaurant.builder().idOwner(ownerId).id(restaurantId).build();

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.of(dish));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> dishUseCase.changeDishStatus(dishId, true));
    }

    @Test
    void changeDishStatus_shouldThrowUnauthorizedOwnerException_whenUnauthorizedOwner() {
        Long ownerId = 1L;
        Long dishId = 5L;
        Long restaurantId = 10L;
        Dish dish = Dish.builder().id(dishId).restaurantId(restaurantId).active(false).build();
        Restaurant restaurant = Restaurant.builder().idOwner(ownerId).id(restaurantId).build();

        when(dishPersistencePort.findDishById(dishId)).thenReturn(Optional.of(dish));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(2L));

        assertThrows(UnauthorizedOwnerException.class,
                () -> dishUseCase.changeDishStatus(dishId, true));
    }

    @Test
    void findAllDishesByRestaurantIdAndCategoryId_shouldReturnList_whenValid() {
        Long restaurantId = 10L;
        Long categoryId = 5L;

        Category category = new Category(1L,"test","test");

        Restaurant restaurant = Restaurant.builder().id(restaurantId).build();

        when(categoryPersistencePort.findCategoryById(categoryId)).thenReturn(Optional.of(category));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(dishPersistencePort.findAllDishesByRestaurantIdAndCategoryId(restaurantId, categoryId, 0, 10))
                .thenReturn(List.of());

        List<Dish> result = dishUseCase.findAllDishesByRestaurantIdAndCategoryId(restaurantId, categoryId, 0, 10);

        assertNotNull(result);
        verify(dishPersistencePort).findAllDishesByRestaurantIdAndCategoryId(restaurantId, categoryId, 0, 10);
    }

    @Test
    void findAllDishesByRestaurantIdAndCategoryId_shouldThrowCategoryNotFoundException_whenCategoryNotFound() {
        Long restaurantId = 10L;
        Long categoryId = 5L;

        when(categoryPersistencePort.findCategoryById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> dishUseCase.findAllDishesByRestaurantIdAndCategoryId(restaurantId, categoryId, 0, 10));
    }

    @Test
    void findAllDishesByRestaurantIdAndCategoryId_shouldThrowRestaurantNotFoundException_whenRestaurantNotFound() {
        Long restaurantId = 10L;
        Long categoryId = 5L;

        Category category = new Category(1L,"test","test");

        when(categoryPersistencePort.findCategoryById(categoryId)).thenReturn(Optional.of(category));
        when(restaurantPersistencePort.findRestaurantById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class,
                () -> dishUseCase.findAllDishesByRestaurantIdAndCategoryId(restaurantId, categoryId, 0, 10));
    }
}