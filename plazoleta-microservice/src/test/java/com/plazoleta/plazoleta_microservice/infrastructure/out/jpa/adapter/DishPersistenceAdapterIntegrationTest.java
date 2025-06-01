package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.spi.IDishPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.CategoryEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.DishEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class DishPersistenceAdapterIntegrationTest {

    @Autowired
    private IDishPersistencePort dishPersistencePort;

    @Autowired
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Autowired
    private EntityManager entityManager;

    private RestaurantEntity testRestaurant;
    private CategoryEntity testCategory;
    private DishEntity testDish;

    @BeforeEach
    void setup() {
        testRestaurant = new RestaurantEntity();
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setNit("12312");
        testRestaurant.setAddress("fasd");
        testRestaurant.setPhoneNumber("+56465");
        testRestaurant.setIdOwner(123L);
        testRestaurant.setUrlLogo("fdf");
        entityManager.persist(testRestaurant);

        testCategory = new CategoryEntity();
        testCategory.setName("Main");
        testCategory.setDescription("Main dishes");
        entityManager.persist(testCategory);

        testDish = new DishEntity();
        testDish.setName("Original Dish");
        testDish.setDescription("Original description");
        testDish.setPrice(10.0);
        testDish.setRestaurant(testRestaurant);
        testDish.setCategory(testCategory);
        testDish.setImageUrl("original.jpg");
        testDish.setImageUrl("dfas");
        entityManager.persist(testDish);
    }

    @Test
    void givenDishExists_whenUpdateDish_thenDishIsUpdated() {
        Dish updatedDish = Dish.builder()
                .id(testDish.getId())
                .name(testDish.getName())
                .description("Updated description")
                .price(15.0)
                .restaurantId(testDish.getRestaurant().getId())
                .category(new Category(testCategory.getId(),testCategory.getName(),testCategory.getDescription()))
                .imageUrl(testDish.getImageUrl())
                .build();

        dishPersistencePort.updateDish(updatedDish);

        Dish dishFromDb = dishPersistencePort.getById(testDish.getId());
        assertEquals("Updated description", dishFromDb.getDescription());
        assertEquals(15.0, dishFromDb.getPrice());
        assertEquals(testDish.getName(), dishFromDb.getName());
    }

    @Test
    void whenGetByIdWithInvalidId_thenThrowsException() {
        Long invalidId = 999L;

        assertThrows(DishNotFoundException.class, () -> {
            dishPersistencePort.getById(invalidId);
        });
    }
}
