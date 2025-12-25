package com.voyzr.agw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/voyzr/trips/**")
                        .filters(f->f.rewritePath(
                                "/voyzr/trips/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-response-Time", new Date().toString()))
                                .uri("lb://TRIPMS")
                )
                .route(p -> p
                        .path("/voyzr/users/**")
                        .filters(f->f.rewritePath(
                                        "/voyzr/users/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-response-Time", new Date().toString()))
                        .uri("lb://USERMS")
                )
                .build();
    }

}
