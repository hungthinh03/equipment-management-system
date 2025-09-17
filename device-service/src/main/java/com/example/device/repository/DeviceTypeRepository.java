package com.example.device.repository;

import com.example.device.model.DeviceType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface DeviceTypeRepository extends ReactiveCrudRepository<DeviceType, Integer> {

    @Query("""
    SELECT dt.* FROM device_types dt
    JOIN device_categories dc ON dt.category_id = dc.id
    WHERE dt.name = :name AND dc.allowed_role = :role
    """)
    Mono<DeviceType> findByNameAndManagedBy(String name, String role);
}
