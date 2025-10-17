package com.example.request.repository;


import com.example.request.dto.RequestDTO;
import com.example.request.model.Request;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RequestRepository extends ReactiveCrudRepository<Request, Integer> {
    Flux<Request> findByRequesterId(Integer integer);

    Flux<Request> findByReturnSubmittedAtIsNotNullAndStatus(String status);

    Flux<Request> findAllByStatus(String status);

    @Query("SELECT r.*, rg.* " +
            "FROM requests r " +
            "LEFT JOIN registries rg ON r.id = rg.request_id " + // Left join = also keep non-registries
            "WHERE r.status = 'PENDING' AND r.processed_by_manager IS NULL " +
            "ORDER BY CASE WHEN r.request_type = 'ASSIGN' THEN 0 ELSE 1 END, r.created_at DESC")
    Flux<RequestDTO> findAllPendingRequestsForManager();

    @Query("SELECT r.*, rg.* " +
            "FROM requests r " +
            "LEFT JOIN registries rg ON r.id = rg.request_id " +
            "WHERE r.status = 'PENDING' AND r.processed_by_manager IS NOT NULL " +
            "ORDER BY CASE WHEN r.request_type = 'ASSIGN' THEN 0 ELSE 1 END, r.created_at DESC")
    Flux<RequestDTO> findAllPendingRequestsForIT();

    @Query("SELECT r.*, rg.* " +
            "FROM requests r " +
            "LEFT JOIN registries rg ON r.id = rg.request_id " +
            "WHERE r.id = :id")
    Mono<RequestDTO> findRequestById(Integer id);

    @Query("SELECT r.*, rg.* " +
            "FROM requests r " +
            "LEFT JOIN registries rg ON r.id = rg.request_id " +
            "WHERE r.processed_by_manager IS NOT NULL")
    Flux<RequestDTO> findRequestByProcessedByManagerIsNotNull();

    @Query("SELECT r.*, rg.* " +
            "FROM requests r " +
            "LEFT JOIN registries rg ON r.id = rg.request_id " +
            "WHERE r.return_submitted_at IS NOT NULL " +
            "AND r.status = :status")
    Flux<RequestDTO> findRequestByReturnSubmittedAtIsNotNullAndStatus(String status);
}
