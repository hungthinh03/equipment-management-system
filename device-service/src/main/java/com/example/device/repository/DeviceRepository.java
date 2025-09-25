package com.example.device.repository;

import com.example.device.dto.SearchResultDTO;
import com.example.device.dto.ViewDeviceDTO;
import com.example.device.model.Device;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Repository
public interface DeviceRepository extends ReactiveCrudRepository<Device, Integer>{

    @Query("SELECT d.id, d.uuid, d.name, dt.name AS type, d.status, d.assigned_to, d.created_at, d.updated_at " +
            "FROM devices d " +
            "JOIN device_types dt ON d.type_id = dt.id " +
            "JOIN device_categories dc ON dt.category_id = dc.id " +
            "WHERE d.id = :id AND dc.managed_by = :role")
    Mono<ViewDeviceDTO> findViewByIdAndManagedBy(Integer id, String role);

    @Query("SELECT d.* " +
            "FROM devices d " +
            "JOIN device_types dt ON d.type_id = dt.id " +
            "JOIN device_categories dc ON dt.category_id = dc.id " +
            "WHERE d.id = :id AND dc.managed_by = :role")
    Mono<Device> findDeviceByIdAndManagedBy(Integer id, String role);

    @Query("SELECT d.id, d.uuid, d.name, dt.name AS type, d.status, d.assigned_to, d.created_at, d.updated_at " +
            "FROM devices d " +
            "JOIN device_types dt ON d.type_id = dt.id " +
            "JOIN device_categories dc ON dt.category_id = dc.id " +
            "WHERE dc.managed_by = :role")
    Flux<ViewDeviceDTO> findAllByManagedBy(String role);


    @Query("SELECT d.uuid, d.name, dt.name AS type, dc.name AS category, d.status, d.updated_at " +
            "FROM devices d " +
            "JOIN device_types dt ON d.type_id = dt.id " +
            "JOIN device_categories dc ON dt.category_id = dc.id " +
            "WHERE (:name IS NULL OR d.name ILIKE CONCAT('%', :name, '%')) " +
            "AND (:type IS NULL OR dt.name ILIKE :type)")
    Flux<SearchResultDTO> searchByParameter(@Param("name") String name,
                                            @Param("type") String type);

    @Query("SELECT d.uuid, d.name, dt.name AS type, dc.name AS category, d.status, d.updated_at " +
            "FROM devices d " +
            "JOIN device_types dt ON d.type_id = dt.id " +
            "JOIN device_categories dc ON dt.category_id = dc.id " +
            "WHERE d.uuid = :uuid")
    Mono<SearchResultDTO> searchByUuid(UUID uuid);
}

