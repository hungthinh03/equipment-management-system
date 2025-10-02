package com.example.request.repository;


import com.example.request.model.Request;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RequestRepository extends ReactiveCrudRepository<Request, Integer> {
    Flux<Request> findByRequesterId(Integer integer);

    Flux<Request> findByStatusAndProcessedByManagerIsNotNull(String status);

    Flux<Request> findByStatusAndProcessedByManagerIsNull(String status);

    Flux<Request> findByRequestedToCloseAtIsNotNull();

    Flux<Request> findByProcessedByManagerIsNotNull();
}
