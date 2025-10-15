package com.example.request.repository;


import com.example.request.dto.MyRegistryDTO;
import com.example.request.model.Registry;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RegistryRepository extends ReactiveCrudRepository<Registry, Integer> {

    @Query("SELECT r.id AS request_id, " +
            "rg.name, rg.type, rg.serial_number, rg.manufacturer, " +
            "r.reason, r.status, r.created_at, r.updated_at " +
            "FROM requests r " +
            "JOIN registries rg " +
            "ON r.id = rg.request_id " +
            "WHERE r.requester_id = :requesterId " +
            "ORDER BY r.created_at DESC")
    Flux<MyRegistryDTO> findAllRegistryByRequesterId(Integer requesterId);

    @Query("SELECT r.id AS request_id, " +
            "rg.name, rg.type, rg.serial_number, rg.manufacturer, " +
            "r.reason, r.status, r.created_at, r.updated_at " +
            "FROM requests r " +
            "JOIN registries rg " +
            "ON r.id = rg.request_id " +
            "WHERE rg.request_id = :requestId")
    Mono<MyRegistryDTO> findByRequestId(Integer requestId);

    Mono<Registry> getDeviceTypeByRequestId(Integer id);
}
