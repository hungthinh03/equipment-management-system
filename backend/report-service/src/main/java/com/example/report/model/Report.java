package com.example.report.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "report_name")
    private String reportName;

    @Column(name = "format")
    private String format; // PDF, CSV, ect

    @Column(name = "generated_by")
    private Integer generatedBy;

    @Column(name = "generated_at", insertable = false, updatable = false) // auto gen by DB
    private Instant generatedAt;

    @Column(name = "record_count")
    private Integer recordCount;

    @Column(name = "notes")
    private String notes;


    public Report(String reportName, String format, Integer generatedBy, Integer recordCount) {
        this.reportName = reportName;
        this.format = format;
        this.generatedBy = generatedBy;
        this.recordCount = recordCount;
    }
}
