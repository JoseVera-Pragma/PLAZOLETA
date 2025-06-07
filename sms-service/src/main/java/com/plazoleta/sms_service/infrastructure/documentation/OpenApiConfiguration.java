package com.plazoleta.sms_service.infrastructure.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
            .components(new Components())
            .info(new Info()
                .title("SMS Microservice")
                .version("1.0")
                .description("API documentation")
                .termsOfService("http://swagger.io/terms/")
                .license(new License().name("Apache 2.0").url("http://springdoc.org"))
            );
    }
}