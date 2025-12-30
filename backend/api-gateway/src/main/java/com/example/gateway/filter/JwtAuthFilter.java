package com.example.gateway.filter;

import com.example.gateway.common.enums.ErrorCode;
import com.example.gateway.dto.ApiResponseDTO;
import com.example.gateway.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class JwtAuthFilter implements GatewayFilterFactory<JwtAuthFilter.Config> {
    private final JwtUtil jwtUtil;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> Mono.defer(() -> { //wait until subscription
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.isTokenValid(token)) {
                return unauthorized(exchange);
            }

            // Extract claims and inject as headers
            String role = jwtUtil.extractRole(token);
            String id = jwtUtil.extractSubject(token);

            // Mutate the request with headers
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Role", role)
                    .header("X-User-Id", id)
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            return chain.filter(mutatedExchange);
        });
    }


    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return Mono.defer(() -> { //wait until subscription
            try {
                byte[] bytes = new ObjectMapper().writeValueAsBytes(new ApiResponseDTO(ErrorCode.INVALID_TOKEN));
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                return exchange.getResponse().writeWith(Mono.just(buffer));
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
        });
    }

    public static class Config {}
}
