package com.voyzr.tripms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class OpenAPIConfig {

    @Bean
    public OpenAPI tripMicroserviceOpenAPI() {
        log.info("Creating OpenAPI bean for Trip Microservice");
        return new OpenAPI()
                .info(new Info().title("Trip Microservice API")
                        .description("API documentation for Trip Microservice")
                        .version("1.0"));
    }
}
