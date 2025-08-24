package com.proyecto.backend.e_commerce.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "change_timestamp", nullable = false)
    private LocalDateTime changeTimestamp = LocalDateTime.now();

    @Column(name = "details", columnDefinition = "text")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String details;


    public AuditLog(String action, String entityName, Long entityId, String changedBy, String details) {
        this.action = action;
        this.entityName = entityName;
        this.entityId = entityId;
        this.changedBy = changedBy;
        this.details = details;
    }

    public AuditLog() {
    }
}
