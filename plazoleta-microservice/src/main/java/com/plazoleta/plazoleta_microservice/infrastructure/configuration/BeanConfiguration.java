package com.plazoleta.plazoleta_microservice.infrastructure.configuration;

import com.plazoleta.plazoleta_microservice.application.handler.ICategoryHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
import com.plazoleta.plazoleta_microservice.application.handler.impl.CategoryHandlerImpl;
import com.plazoleta.plazoleta_microservice.application.handler.impl.DishHandlerImpl;
import com.plazoleta.plazoleta_microservice.application.handler.impl.RestaurantHandlerImpl;
import com.plazoleta.plazoleta_microservice.application.mapper.*;
import com.plazoleta.plazoleta_microservice.domain.api.ICategoryServicePort;
import com.plazoleta.plazoleta_microservice.domain.api.IDishServicePort;
import com.plazoleta.plazoleta_microservice.domain.api.IRestaurantServicePort;
import com.plazoleta.plazoleta_microservice.domain.spi.ICategoryPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IDishPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IUserSecurityPort;
import com.plazoleta.plazoleta_microservice.domain.usecase.CategoryUseCase;
import com.plazoleta.plazoleta_microservice.domain.usecase.DishUseCase;
import com.plazoleta.plazoleta_microservice.domain.usecase.RestaurantUseCase;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feing.IUserFeignClient;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feing.UserFeignAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.CategoryJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.ICategoryEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.ICategoryRepository;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IDishRepository;
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
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;
    private final ICategoryRequestMapper categoryRequestMapper;
    private final ICategoryResponseMapper categoryResponseMapper;

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
        return new RestaurantHandlerImpl(restaurantServicePort, restaurantRequestMapper, restaurantResponseMapper);
    }

    @Bean
    public ICategoryPersistencePort categoryPersistencePort() {
        return new CategoryJpaAdapter(categoryRepository, dishRepository,categoryEntityMapper);
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
    public IDishHandler dishHandler(IDishServicePort dishServicePort, ICategoryServicePort categoryServicePort){
        return new DishHandlerImpl(dishServicePort, categoryServicePort,dishRequestMapper,dishResponseMapper);
    }

}