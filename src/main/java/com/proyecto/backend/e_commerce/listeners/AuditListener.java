package com.proyecto.backend.e_commerce.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyecto.backend.e_commerce.domain.AuditLog;
import com.proyecto.backend.e_commerce.repository.AuditLogRepository;
import com.proyecto.backend.e_commerce.utils.SpringCtx;
import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.*;
import java.lang.reflect.Method;


public class AuditListener {


    private static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(new JavaTimeModule());

    private static final Map<Object, String> BEFORE = new WeakHashMap<>();

    private static final Set<String> SENSITIVE = Set.of("password", "accessToken", "refreshToken");

    @PostLoad
    public void onLoad(Object entity) {
        BEFORE.put(entity, toJson(sanitize(unproxy(entity))));
    }

    @PostPersist
    public void onCreate(Object entity) {
        Object cleaned = sanitize(unproxy(entity));
        save(entity, "CREATE", null, toJson(cleaned));
    }

    @PostUpdate
    public void onUpdate(Object entity) {
        Object e = unproxy(entity);
        String before = BEFORE.get(entity);
        Object cleaned = sanitize(e);
        String after = toJson(cleaned);

        if (sameJson(before, after)) return;
        save(e, "UPDATE", before, after);
        BEFORE.put(entity, after);
    }

    @PreRemove
    public void onDelete(Object entity) {
        Object e = unproxy(entity);
        String before = BEFORE.getOrDefault(entity, toJson(sanitize(e)));
        save(e, "DELETE", before, null);
    }

    private void save(Object entity, String action, String before, String after) {
        try {
            AuditLogRepository repo = SpringCtx.getBean(AuditLogRepository.class);

            AuditLog log = new AuditLog();
            log.setAction(action);
            log.setEntityName(entity.getClass().getSimpleName());
            log.setEntityId(resolveId(entity));
            log.setChangedBy(resolveUser());
            log.setDetails(buildDetails(before, after));

            repo.save(log);
        } catch (Exception ignored) { }
    }


    private boolean sameJson(String a, String b) {
        try {
            if (Objects.equals(a, b)) return true;
            if (a == null || b == null) return false;
            return MAPPER.readTree(a).equals(MAPPER.readTree(b));
        } catch (Exception ex) {
            return Objects.equals(a, b);
        }
    }


    private Object sanitize(Object entity) {
        try {
            Map<String, Object> map = MAPPER.convertValue(
                    entity, new TypeReference<LinkedHashMap<String, Object>>() {});

            for (String k : SENSITIVE) map.remove(k);


            flattenIdOnly(map, "user");
            flattenIdOnly(map, "product");
            flattenIdOnly(map, "order");
            flattenIdOnly(map, "inventory");
            flattenIdsInCollection(map, "roles");

            return map;
        } catch (Exception ex) {
            return entity;
        }
    }

    @SuppressWarnings("unchecked")
    private void flattenIdOnly(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof Map<?, ?> nested) {
            Object id = ((Map<String, Object>) nested).get("id");
            Map<String, Object> onlyId = new LinkedHashMap<>();
            onlyId.put("id", id);
            map.put(key, onlyId);
        }
    }

    @SuppressWarnings("unchecked")
    private void flattenIdsInCollection(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof Collection<?> col) {
            List<Object> ids = new ArrayList<>();
            for (Object o : col) {
                if (o instanceof Map<?, ?> nm) {
                    ids.add(((Map<String, Object>) nm).get("id"));
                }
            }
            map.put(key, ids);
        }
    }

    private String buildDetails(String before, String after) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (before != null) payload.put("before", readTreeOrRaw(before));
        if (after  != null) payload.put("after",  readTreeOrRaw(after));
        return toJson(payload);
    }

    private Long resolveId(Object entity) {
        try {
            Method m = entity.getClass().getMethod("getId");
            Object v = m.invoke(entity);
            return v == null ? null : Long.valueOf(v.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null ? auth.getName() : "system";
        } catch (Exception e) {
            return "system";
        }
    }

    private String toJson(Object o) {
        try { return MAPPER.writeValueAsString(o); }
        catch (Exception e) { return null; }
    }

    private Object readTreeOrRaw(String json) {
        try { return MAPPER.readTree(json); }
        catch (Exception e) { return json; }
    }

    private Object unproxy(Object entity) {
        if (entity instanceof HibernateProxy p) {
            return p.getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }
}
