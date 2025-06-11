package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantJpaAdapterTest {

    @Mock
    private IRestaurantRepository restaurantRepository;

    @Mock
    private IRestaurantEntityMapper restaurantEntityMapper;

    @InjectMocks
    private RestaurantJpaAdapter restaurantJpaAdapter;

    private Restaurant domainRestaurant;
    private RestaurantEntity entityRestaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        domainRestaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante El Sabor")
                .nit("123456789")
                .address("Calle 123")
                .phoneNumber("3201234567")
                .urlLogo("logo.png")
                .idOwner(99L)
                .build();

        entityRestaurant = new RestaurantEntity();
        entityRestaurant.setId(1L);
        entityRestaurant.setName("Restaurante El Sabor");
        entityRestaurant.setNit("123456789");
        entityRestaurant.setAddress("Calle 123");
        entityRestaurant.setPhoneNumber("3201234567");
        entityRestaurant.setUrlLogo("logo.png");
        entityRestaurant.setIdOwner(99L);
    }

    @Test
    void testSaveRestaurant() {
        when(restaurantEntityMapper.toRestaurantEntity(domainRestaurant)).thenReturn(entityRestaurant);
        when(restaurantRepository.save(entityRestaurant)).thenReturn(entityRestaurant);
        when(restaurantEntityMapper.toRestaurant(entityRestaurant)).thenReturn(domainRestaurant);

        Restaurant result = restaurantJpaAdapter.saveRestaurant(domainRestaurant);

        assertEquals(domainRestaurant, result);
        verify(restaurantRepository).save(entityRestaurant);
    }

    @Test
    void testExistsRestaurantByNitTrue() {
        when(restaurantRepository.existsByNit("123456789")).thenReturn(true);
        assertTrue(restaurantJpaAdapter.existsRestaurantByNit("123456789"));
    }

    @Test
    void testExistsRestaurantByNitFalse() {
        when(restaurantRepository.existsByNit("123456789")).thenReturn(false);
        assertFalse(restaurantJpaAdapter.existsRestaurantByNit("123456789"));
    }

    @Test
    void testFindRestaurantByIdFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(entityRestaurant));
        when(restaurantEntityMapper.toRestaurant(entityRestaurant)).thenReturn(domainRestaurant);

        Optional<Restaurant> result = restaurantJpaAdapter.findRestaurantById(1L);

        assertTrue(result.isPresent());
        assertEquals(domainRestaurant, result.get());
    }

    @Test
    void testFindRestaurantByIdNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Restaurant> result = restaurantJpaAdapter.findRestaurantById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllRestaurants() {
        int pageIndex = 0;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        RestaurantEntity entity1 = new RestaurantEntity(1L, "Rest1", "Address1", "NIT1", "123", "logo1.png", 1L);
        RestaurantEntity entity2 = new RestaurantEntity(2L, "Rest2", "Address2", "NIT2", "456", "logo2.png", 2L);
        List<RestaurantEntity> entities = List.of(entity1, entity2);

        Restaurant domain1 = Restaurant.builder().id(1L).name("Rest1").urlLogo("logo1.png").build();
        Restaurant domain2 = Restaurant.builder().id(2L).name("Rest2").urlLogo("logo2.png").build();

        org.springframework.data.domain.Page<RestaurantEntity> springPage =
                new PageImpl<>(entities, pageable, 10);

        when(restaurantRepository.findAll(pageable)).thenReturn(springPage);
        when(restaurantEntityMapper.toRestaurant(entity1)).thenReturn(domain1);
        when(restaurantEntityMapper.toRestaurant(entity2)).thenReturn(domain2);

        Page<Restaurant> result = restaurantJpaAdapter.findAllRestaurants(pageIndex, pageSize);

        assertEquals(2, result.getContent().size());
        assertEquals("Rest1", result.getContent().get(0).getName());
        assertEquals("Rest2", result.getContent().get(1).getName());
        assertEquals(10, result.getTotalElements());
        assertEquals(0, result.getPageNumber());
        assertEquals(2, result.getPageSize());

        verify(restaurantRepository).findAll(pageable);
        verify(restaurantEntityMapper).toRestaurant(entity1);
        verify(restaurantEntityMapper).toRestaurant(entity2);
    }
}