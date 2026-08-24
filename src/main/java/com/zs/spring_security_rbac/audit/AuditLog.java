package com.zs.spring_security_rbac.audit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String userEmail;
    private String userRole;
    private String httpMethod;
    private String endpoint;
    private int statusCode;
    private String ipAddress;
    private LocalDateTime accessedAt;

    @PrePersist
    public void prePersist() {
        this.accessedAt = LocalDateTime.now();
    }
}
