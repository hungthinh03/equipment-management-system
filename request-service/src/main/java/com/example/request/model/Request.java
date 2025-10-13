package com.example.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("requests")
public class Request {
    @Id
    @Column("id")
    private Integer id;

    @Column("request_type")
    private String requestType;     // ASSIGN or REGISTER

    @Column("device_uuid")
    private UUID deviceUuid;         // Public UUID for employee-facing API

    @Column("requester_id")
    private Integer requesterId;     // Employee ID

    @Column("reason")
    private String reason;

    @Column("status")
    private String status;           // PENDING, APPROVED, REJECTED, CLOSED

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Column("processed_by_manager")
    private Integer processedByManager;

    @Column("manager_processed_at")
    private Instant managerProcessedAt;

    @Column("processed_by_it")
    private Integer processedByIt;

    @Column("it_processed_at")
    private Instant itProcessedAt;

    @Column("manager_comment")
    private String managerComment;

    @Column("it_comment")
    private String itComment;

    @Column("delivered_by")
    private Integer deliveredBy;

    @Column("delivered_at")
    private Instant deliveredAt;

    @Column("return_submitted_at")
    private Instant returnSubmittedAt;

    @Column("closed_by")
    private Integer closedBy;

    public Request(String requestType, String uuid, Integer userId, String reason)
    {
        this.requestType = requestType;
        this.deviceUuid = UUID.fromString(uuid); //convert to uuid for db save
        this.requesterId = userId;
        this.reason = reason;
    }

    public Request(String requestType, Integer userId, String reason)
    {
        this.requestType = requestType;
        this.requesterId = userId;
        this.reason = reason;
    }
}

