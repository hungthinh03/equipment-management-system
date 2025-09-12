package com.example.device.config;

import com.example.device.security.JwtAuthenticationManager;
import com.example.device.security.JwtSecurityContextRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@AllArgsConstructor
@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationManager authManager;
    private final JwtSecurityContextRepository contextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        //.pathMatchers("/auth/**").permitAll()   // if use /auth endpoint
                        .anyExchange().authenticated()         // everything requires JWT
                )
                .authenticationManager(authManager)
                .securityContextRepository(contextRepository)
                .build();
    }
}

