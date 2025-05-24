package com.plazoleta.user_microservice.infrastructure.configuration;

import com.plazoleta.user_microservice.domain.api.IRoleServicePort;
import com.plazoleta.user_microservice.domain.api.IUserServicePort;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import com.plazoleta.user_microservice.domain.usecase.RoleUseCase;
import com.plazoleta.user_microservice.domain.usecase.UserUseCase;
import com.plazoleta.user_microservice.domain.validation.UserValidator;
import com.plazoleta.user_microservice.infrastructure.out.jpa.adapter.RoleJpaAdapter;
import com.plazoleta.user_microservice.infrastructure.out.jpa.adapter.UserJpaAdapter;
import com.plazoleta.user_microservice.infrastructure.out.jpa.mapper.IRoleEntityMapper;
import com.plazoleta.user_microservice.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IRoleRepository;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final IUserRepository iUserRepository;
    private final IUserEntityMapper iUserEntityMapper;
    private final IRoleRepository iRoleRepository;
    private final IRoleEntityMapper iRoleEntityMapper;

    @Bean
    public IUserPersistencePort iUserPersistencePort(){
        return new UserJpaAdapter(iUserRepository, iUserEntityMapper);
    }

    @Bean
    public IRolePersistencePort iRolePersistencePort(){
        return new RoleJpaAdapter(iRoleRepository,iUserRepository,iRoleEntityMapper);
    }


    @Bean
    public UserValidator userValidator(){
        return new UserValidator(iUserPersistencePort());
    }

    @Bean
    public IUserServicePort iUserServicePort(){
        return new UserUseCase(iUserPersistencePort(), userValidator());
    }

    @Bean
    public IRoleServicePort iRoleServicePort(){
        return new RoleUseCase(iRolePersistencePort());
    }

}