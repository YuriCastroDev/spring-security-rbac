package com.zs.spring_security_rbac.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogRepository.findAll());
    }

    @GetMapping("/user/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AuditLog>> getLogsByUser(@PathVariable String email) {
        return ResponseEntity.ok(auditLogRepository.findByUserEmailOrderByAccessedAtDesc(email));
    }

    @GetMapping("/denied")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AuditLog>> getDeniedAccesses() {
        return ResponseEntity.ok(auditLogRepository.findByStatusCodeOrderByAccessedAtDesc(403));
    }
}
