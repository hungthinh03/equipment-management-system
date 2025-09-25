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

    @Column("approved_by_manager")
    private Integer approvedByManager;

    @Column("manager_approved_at")
    private Instant managerApprovedAt;

    @Column("approved_by_it")
    private Integer approvedByIt;

    @Column("it_approved_at")
    private Instant itApprovedAt;

    @Column("manager_comment")
    private String managerComment;

    @Column("it_comment")
    private String itComment;

    public Request(UUID uuid, Integer userId, String reason)
    {
        this.deviceUuid = uuid;
        this.requesterId = userId;
        this.reason = reason;
    }
}

