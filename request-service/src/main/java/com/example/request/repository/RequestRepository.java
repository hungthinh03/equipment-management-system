package com.example.request.repository;


import com.example.request.model.Request;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends ReactiveCrudRepository<Request, Integer> {
}
