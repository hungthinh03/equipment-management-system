package com.example.request.repository;


import com.example.request.model.Registry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistryRepository extends ReactiveCrudRepository<Registry, Integer> {
}
