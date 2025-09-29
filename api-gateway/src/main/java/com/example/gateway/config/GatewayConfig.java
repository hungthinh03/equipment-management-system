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
                .route("device-service-external-request", r -> r.path("/device/**")
                        .and().header("X-Service-Source", "request-service") // if is from Request Service
                        .and().not(h -> h.header("X-Processed-Route", "true"))
                        .filters(f -> f
                                .removeRequestHeader("X-*")
                                .filter(jwtAuthFilter.apply(new JwtAuthFilter.Config()))
                                .addRequestHeader("X-Service-Source", "request-service")
                                .addRequestHeader("X-Processed-Route", "true")
                        )
                        .uri("http://localhost:8083"))
                .route("device-service", r -> r.path("/device/internal/**")
                        .filters(f -> f
                                .removeRequestHeader("X-*") // strip client's custom headers
                                .filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())) // inject safe headers
                        )
                        .uri("http://localhost:8083")) // JWT required
                .route("request-service", r -> r.path("/request/**")
                        .filters(f -> f
                                .removeRequestHeader("X-*")
                                .filter(jwtAuthFilter.apply(new JwtAuthFilter.Config()))  // JWT required
                        )
                        .uri("http://localhost:8084"))
                .build();
    }
}
