package com.zs.spring_security_rbac.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByUserEmailOrderByAccessedAtDesc(String email);
    List<AuditLog> findByStatusCodeOrderByAccessedAtDesc(int statusCode);
}
