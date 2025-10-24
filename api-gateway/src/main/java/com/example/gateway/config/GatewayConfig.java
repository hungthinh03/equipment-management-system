package com.example.gateway.config;

import com.example.gateway.filter.JwtAuthFilter;
import com.example.gateway.filter.RoleFilter;
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
                        .uri("http://localhost:8082"))  // Replace with localhost for local run
                .route("device-service-external-request", r -> r.path("/device/**")
                        .and().header("X-Service-Source", ".*") // if is from another service
                        .and().not(h -> h.header("X-Processed-Route", "true"))
                        .filters(f -> f
                                .removeRequestHeader("X-*")
                                .filter(jwtAuthFilter.apply(new JwtAuthFilter.Config()))
                                .filter((exchange, chain) -> {
                                    String source = exchange.getRequest()
                                            .getHeaders().getFirst("X-Service-Source");

                                    return chain.filter(exchange.mutate()
                                            .request(exchange.getRequest().mutate()
                                                    .header("X-Service-Source",
                                                            "request-service".equalsIgnoreCase(source)
                                                                    ? "request-service"
                                                                    : "report-service"
                                                    )
                                                    .header("X-Processed-Route", "true")
                                                    .build())
                                            .build());
                                })
                        )
                        .uri("http://localhost:8083"))
                .route("device-service", r -> r.path("/device/**")
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
                .route("report-service", r -> r.path("/report/**")
                        .filters(f -> f
                                .removeRequestHeader("X-*")
                                .filter(jwtAuthFilter.apply(new JwtAuthFilter.Config()))  // JWT required
                                .filter(new RoleFilter("ADMIN"))
                        )
                        .uri("http://localhost:8085"))
                .build();
    }
}
