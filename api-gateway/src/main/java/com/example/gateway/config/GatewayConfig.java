package com.example.gateway.config;

import com.example.gateway.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;


@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder, JwtAuthFilter jwtAuthFilter) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("http://localhost:8082"))  // no JWT needed
                .route("device-service", r -> r.path("/device/**")
                        .filters(f -> f
                                .removeRequestHeader("X-*") // strip client's potential custom headers
                                .filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())) // inject safe headers
                        )
                        .uri("http://localhost:8083")) // JWT required
                .build();
    }
}
