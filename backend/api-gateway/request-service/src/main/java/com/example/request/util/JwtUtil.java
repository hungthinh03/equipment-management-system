package com.example.request.util;

import com.example.request.common.enums.ErrorCode;
import com.example.request.common.exception.AppException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtUtil {
    public Mono<String> extractToken(String authHeader) {
        return Mono.justOrEmpty(authHeader)
                .filter(header -> header.startsWith("Bearer "))
                .map(h -> h.substring(7)) //strip out 7 characters: "Bearer "
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)));
    }
}
