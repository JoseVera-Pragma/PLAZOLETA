package com.plazoleta.plazoleta_microservice.infrastructure.configuration;

import com.plazoleta.plazoleta_microservice.application.handler.ICategoryHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IOrderHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
import com.plazoleta.plazoleta_microservice.application.handler.impl.*;
import com.plazoleta.plazoleta_microservice.application.mapper.*;
import com.plazoleta.plazoleta_microservice.domain.api.ICategoryServicePort;
import com.plazoleta.plazoleta_microservice.domain.api.IDishServicePort;
import com.plazoleta.plazoleta_microservice.domain.api.IOrderServicePort;
import com.plazoleta.plazoleta_microservice.domain.api.IRestaurantServicePort;
import com.plazoleta.plazoleta_microservice.domain.spi.*;
import com.plazoleta.plazoleta_microservice.domain.usecase.CategoryUseCase;
import com.plazoleta.plazoleta_microservice.domain.usecase.DishUseCase;
import com.plazoleta.plazoleta_microservice.domain.usecase.OrderUseCase;
import com.plazoleta.plazoleta_microservice.domain.usecase.RestaurantUseCase;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feing.IUserFeignClient;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feing.UserFeignAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.CategoryJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.OrderJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.ICategoryEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.OrderEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.ICategoryRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IOrderRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;
    private final ICategoryRepository categoryRepository;
    private final ICategoryEntityMapper categoryEntityMapper;
    private final IUserFeignClient userFeignClient;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;
    private final IRestauranteResumenResponseMapper restauranteResumenResponseMapper;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;
    private final ICategoryRequestMapper categoryRequestMapper;
    private final ICategoryResponseMapper categoryResponseMapper;
    private final AuthenticatedUserHandlerImpl authenticatedUserHandler;
    private final IOrderRepository orderRepository;

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IUserSecurityPort userSecurityPort() {
        return new UserFeignAdapter(userFeignClient);
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort(IRestaurantPersistencePort restaurantPersistencePort,
                                                        IUserSecurityPort userSecurityPort) {
        return new RestaurantUseCase(restaurantPersistencePort, userSecurityPort);
    }

    @Bean
    public IRestaurantHandler restaurantHandler(IRestaurantServicePort restaurantServicePort) {
        return new RestaurantHandlerImpl(restaurantServicePort, restaurantRequestMapper, restaurantResponseMapper, restauranteResumenResponseMapper);
    }

    @Bean
    public ICategoryPersistencePort categoryPersistencePort() {
        return new CategoryJpaAdapter(categoryRepository, dishRepository, categoryEntityMapper);
    }

    @Bean
    public ICategoryServicePort categoryServicePort(ICategoryPersistencePort categoryPersistencePort) {
        return new CategoryUseCase(categoryPersistencePort);
    }

    @Bean
    public ICategoryHandler categoryHandler(ICategoryServicePort categoryServicePort) {
        return new CategoryHandlerImpl(categoryServicePort, categoryRequestMapper, categoryResponseMapper);
    }

    @Bean
    public IDishPersistencePort dishPersistencePort() {
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public IDishServicePort dishServicePort(IDishPersistencePort dishPersistencePort, ICategoryPersistencePort categoryPersistencePort, IRestaurantPersistencePort restaurantPersistencePort) {
        return new DishUseCase(categoryPersistencePort, dishPersistencePort, restaurantPersistencePort);
    }

    @Bean
    public IDishHandler dishHandler(IDishServicePort dishServicePort, ICategoryServicePort categoryServicePort) {
        return new DishHandlerImpl(dishServicePort, categoryServicePort, dishRequestMapper, dishResponseMapper, authenticatedUserHandler);
    }

    @Bean
    public OrderEntityMapper orderEntityMapper() {
        return new OrderEntityMapper(dishEntityMapper);
    }

    @Bean
    public OrderRequestMapper orderRequestMapper() {
        return new OrderRequestMapper();
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, dishRepository, restaurantRepository, orderEntityMapper());
    }

    @Bean
    public IOrderServicePort orderServicePort(IOrderPersistencePort orderPersistencePort, IDishPersistencePort dishPersistencePort, IRestaurantPersistencePort restaurantPersistencePort) {
        return new OrderUseCase(orderPersistencePort, dishPersistencePort, restaurantPersistencePort);
    }

    @Bean
    public IOrderHandler orderHandler(IOrderServicePort orderServicePort) {
        return new OrderHandlerImpl(orderServicePort,authenticatedUserHandler, orderRequestMapper());
    }

}