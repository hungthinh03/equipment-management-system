package com.example.gateway.filter;

import com.example.gateway.common.enums.ErrorCode;
import com.example.gateway.dto.ApiResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RoleFilter implements GatewayFilter {

    private final String requiredRole;

    public RoleFilter(String requiredRole) {
        this.requiredRole = requiredRole;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String role = exchange.getRequest().getHeaders().getFirst("X-User-Role");
        if (role == null || !role.equalsIgnoreCase(requiredRole)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return Mono.defer(() -> {
                try {
                    byte[] bytes = new ObjectMapper().writeValueAsBytes(new ApiResponseDTO(ErrorCode.UNAUTHORIZED));
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    return exchange.getResponse().writeWith(Mono.just(buffer));
                } catch (JsonProcessingException e) {
                    return Mono.error(e);
                }
            });
        }
        return chain.filter(exchange);
    }
}
