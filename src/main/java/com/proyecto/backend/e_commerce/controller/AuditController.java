package com.proyecto.backend.e_commerce.controller;


import com.proyecto.backend.e_commerce.domain.AuditLog;
import com.proyecto.backend.e_commerce.repository.AuditLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(
        name = "Auditoría",
        description = "Consulta de eventos de auditoría (CREATE/UPDATE/DELETE) generados por el listener."
)
@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    public AuditController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Operation(
            summary = "Historial de cambios de una entidad",
            description = "Devuelve el historial paginado filtrando por nombre de entidad y su ID."
    )
    @ApiResponse(responseCode = "200", description = "Historial paginado devuelto correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuditPageSchema.class)))
    @GetMapping
    public ResponseEntity<Page<AuditLogDto>> findByEntity(
            @Parameter(description = "Nombre simple de la entidad. Ej: Product, Inventory", required = true, example = "Product")
            @RequestParam String entity,
            @Parameter(description = "ID de la entidad", required = true, example = "42")
            @RequestParam Long entityId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Pageable unsorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.unsorted()
        );

        Page<AuditLogDto> page = auditLogRepository
                .findByEntityNameIgnoreCaseAndEntityIdOrderByChangeTimestampDesc(entity, entityId, unsorted)
                .map(this::toDto);

        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Detalle de un evento de auditoría por su ID")
    @ApiResponse(responseCode = "200", description = "Evento encontrado")
    @ApiResponse(responseCode = "404", description = "No existe el evento")
    @GetMapping("/{id}")
    public ResponseEntity<AuditLogDto> getById(
            @Parameter(description = "ID del evento de auditoría", example = "1001")
            @PathVariable Long id
    ) {
        return auditLogRepository.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Últimos eventos globales",
            description = "Devuelve los últimos eventos (global, sin filtrar por entidad) limitado por tamaño."
    )
    @ApiResponse(responseCode = "200", description = "Listado de eventos recientes",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AuditLogDto.class))))
    @GetMapping("/recent")
    public ResponseEntity<List<AuditLogDto>> recent(
            @Parameter(description = "Cantidad de registros a devolver (1..100)", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        if (size < 1) size = 1;
        if (size > 100) size = 100;
        Page<AuditLog> page = auditLogRepository.findAll(
                PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "changeTimestamp")));
        return ResponseEntity.ok(page.map(this::toDto).getContent());
    }



    private AuditLogDto toDto(AuditLog a) {
        return new AuditLogDto(
                a.getId(),
                a.getAction(),
                a.getEntityName(),
                a.getEntityId(),
                a.getChangedBy(),
                a.getChangeTimestamp(),
                a.getDetails()
        );
    }

    @Schema(name = "AuditLogDto", description = "Evento de auditoría")
    public static class AuditLogDto {
        @Schema(description = "ID del registro", example = "123")
        public Long id;
        @Schema(description = "Acción realizada", example = "UPDATE")
        public String action;
        @Schema(description = "Nombre de la entidad afectada", example = "Product")
        public String entityName;
        @Schema(description = "ID de la entidad afectada", example = "42")
        public Long entityId;
        @Schema(description = "Usuario que realizó el cambio", example = "admin")
        public String changedBy;
        @Schema(description = "Fecha/hora del cambio en ISO-8601")
        public LocalDateTime changeTimestamp;
        @Schema(description = "JSON con 'before' y/o 'after'")
        public String details;

        public AuditLogDto(Long id, String action, String entityName, Long entityId,
                           String changedBy, LocalDateTime changeTimestamp, String details) {
            this.id = id;
            this.action = action;
            this.entityName = entityName;
            this.entityId = entityId;
            this.changedBy = changedBy;
            this.changeTimestamp = changeTimestamp;
            this.details = details;
        }
    }


    @Schema(name = "AuditPage", description = "Página de resultados de auditoría")
    public static class AuditPageSchema {
        @Schema(description = "Contenido de la página")
        public List<AuditLogDto> content;
        @Schema(description = "Número de página (0-index)")
        public int number;
        @Schema(description = "Tamaño de página")
        public int size;
        @Schema(description = "Total de elementos")
        public long totalElements;
        @Schema(description = "Total de páginas")
        public int totalPages;
    }
}
