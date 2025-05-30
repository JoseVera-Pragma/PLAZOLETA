package com.plazoleta.user_microservice.infrastructure.configuration;

import com.plazoleta.user_microservice.domain.api.IAuthServicePort;
import com.plazoleta.user_microservice.domain.api.IRoleServicePort;
import com.plazoleta.user_microservice.domain.api.IUserServicePort;
import com.plazoleta.user_microservice.domain.spi.IPasswordEncoderPort;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import com.plazoleta.user_microservice.domain.spi.ITokenGeneratorPort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import com.plazoleta.user_microservice.domain.usecase.AuthUseCase;
import com.plazoleta.user_microservice.domain.usecase.RoleUseCase;
import com.plazoleta.user_microservice.domain.usecase.UserUseCase;
import com.plazoleta.user_microservice.domain.validation.UserValidator;
import com.plazoleta.user_microservice.infrastructure.configuration.security.adapter.JwtTokenAdapter;
import com.plazoleta.user_microservice.infrastructure.configuration.security.adapter.PasswordEncoderAdapter;
import com.plazoleta.user_microservice.infrastructure.out.jpa.adapter.RoleJpaAdapter;
import com.plazoleta.user_microservice.infrastructure.out.jpa.adapter.UserJpaAdapter;
import com.plazoleta.user_microservice.infrastructure.out.jpa.mapper.IRoleEntityMapper;
import com.plazoleta.user_microservice.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IRoleRepository;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final IUserRepository iUserRepository;
    private final IUserEntityMapper iUserEntityMapper;
    private final IRoleRepository iRoleRepository;
    private final IRoleEntityMapper iRoleEntityMapper;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public IUserPersistencePort iUserPersistencePort() {
        return new UserJpaAdapter(iUserRepository, iUserEntityMapper);
    }

    @Bean
    public IRolePersistencePort iRolePersistencePort() {
        return new RoleJpaAdapter(iRoleRepository, iUserRepository, iRoleEntityMapper);
    }

    @Bean
    public UserValidator userValidator() {
        return new UserValidator(iUserPersistencePort());
    }

    @Bean
    public IUserServicePort iUserServicePort() {
        return new UserUseCase(iUserPersistencePort(), userValidator());
    }

    @Bean
    public IRoleServicePort iRoleServicePort() {
        return new RoleUseCase(iRolePersistencePort());
    }

    @Bean
    public ITokenGeneratorPort tokenGeneratorPort() throws NoSuchAlgorithmException {
        return new JwtTokenAdapter();
    }

    @Bean
    public IPasswordEncoderPort passwordEncoderPort() {
        return new PasswordEncoderAdapter(passwordEncoder);
    }

    @Bean
    public IAuthServicePort authServicePort() throws NoSuchAlgorithmException {
        return new AuthUseCase(iUserPersistencePort(), passwordEncoderPort(), tokenGeneratorPort());
    }

    @Bean
    @Profile("!test")
    public DataInitializer dataInitializer() {
        return new DataInitializer(iRolePersistencePort(), iUserPersistencePort(), passwordEncoderPort());
    }

}