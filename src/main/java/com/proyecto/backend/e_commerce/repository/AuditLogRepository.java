package com.proyecto.backend.e_commerce.repository;

import com.proyecto.backend.e_commerce.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
