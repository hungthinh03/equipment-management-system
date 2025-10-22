package com.example.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("reports")
public class Report {
    @Id
    @Column("id")
    private Integer id;

    @Column("report_name")
    private String reportName;

    @Column("generated_by")
    private Integer generatedBy;

    @Column("generated_at")
    private Instant generatedAt;

    @Column("notes")
    private String notes;

}
