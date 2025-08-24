package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByEntityNameIgnoreCaseOrderByChangeTimestampDesc(
            String entityName, Pageable pageable
    );


    Page<AuditLog> findByEntityNameIgnoreCaseAndEntityIdOrderByChangeTimestampDesc(
            String entityName, Long entityId, Pageable pageable
    );
}
