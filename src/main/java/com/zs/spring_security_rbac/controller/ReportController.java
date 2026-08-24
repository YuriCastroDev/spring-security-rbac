package com.zs.spring_security_rbac.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @GetMapping
    @PreAuthorize("hasAuthority('report:read')")
    public ResponseEntity<Map<String, Object>> getReport() {
        return ResponseEntity.ok(Map.of(
                "totalUsers", 150,
                "totalOrders", 3420,
                "revenue", 125000.00,
                "period", "2026-06"
        ));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('report:export')")
    public ResponseEntity<Map<String, String>> exportReport() {
        return ResponseEntity.ok(Map.of(
                "message", "Report exported successfully",
                "format", "CSV",
                "url", "/downloads/report-2026-06.csv"
        ));
    }
}
