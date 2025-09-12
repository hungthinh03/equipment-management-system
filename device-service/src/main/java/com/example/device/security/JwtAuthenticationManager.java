package com.example.device.security;

import com.example.device.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    //Validate the token and convert the role claim into a Spring Security authority
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        if (!jwtUtil.isTokenValid(token)) {
            return Mono.empty(); // invalid token
        }

        String email = jwtUtil.extractSubject(token);
        String role = jwtUtil.extractRole(token); // e.g. ADMIN, IT


        return Mono.just(new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)) // required for @PreAuthorize
        ));

    }
}
