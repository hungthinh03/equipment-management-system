package com.example.auth.repository;

import com.example.auth.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Integer>{
    Mono<User> findByEmail(String email);
}
