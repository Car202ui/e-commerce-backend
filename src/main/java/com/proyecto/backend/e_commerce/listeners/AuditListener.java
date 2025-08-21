package com.proyecto.backend.e_commerce.listeners;

import com.proyecto.backend.e_commerce.domain.AuditLog;
import com.proyecto.backend.e_commerce.domain.Product;
import com.proyecto.backend.e_commerce.repository.AuditLogRepository;
import com.proyecto.backend.e_commerce.utils.BeanUtil;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuditListener {
    private static final Logger logger = LoggerFactory.getLogger(AuditListener.class);

    @PostPersist
    public void onPostPersist(Object entity) {
        logAudit("CREATE", entity);
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        logAudit("UPDATE", entity);
    }

    private void logAudit(String action, Object entity) {
        try {
            AuditLogRepository auditLogRepository = BeanUtil.getBean(AuditLogRepository.class);
            String changedBy = getCurrentUsername();
            String entityName = entity.getClass().getSimpleName();
            Long entityId = null;
            String details = entity.toString();

            if (entity instanceof Product) {
                entityId = ((Product) entity).getId();
            }

            AuditLog auditLog = new AuditLog(action, entityName, entityId, changedBy, details);
            auditLogRepository.save(auditLog);

            logger.info("Auditoría registrada exitosamente: [Acción: {}, Entidad: {}, ID: {}, Por: {}]",
                    action, entityName, entityId, changedBy);

        } catch (Exception e) {

            logger.error("Error fatal al registrar la auditoría. Causa: {}", e.getMessage(), e);
        }
    }

    private String getCurrentUsername() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            return principal.toString();
        } catch (Exception e) {
            return "system";
        }
    }
}
