package com.example.device.repository;

import com.example.device.model.DeviceType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface DeviceTypeRepository extends ReactiveCrudRepository<DeviceType, Integer> {
    Mono<DeviceType> findByName(String type);
}
