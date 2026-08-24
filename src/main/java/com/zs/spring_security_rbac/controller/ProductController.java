package com.zs.spring_security_rbac.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // Simulação de produtos em memória para demonstração
    private static final List<Map<String, Object>> PRODUCTS = List.of(
            Map.of("id", 1, "name", "Teclado Mecânico", "price", 450.00),
            Map.of("id", 2, "name", "Mouse Gamer", "price", 250.00),
            Map.of("id", 3, "name", "Monitor 4K", "price", 2800.00)
    );

    @GetMapping
    @PreAuthorize("hasAuthority('product:read')")
    public ResponseEntity<List<Map<String, Object>>> listProducts() {
        return ResponseEntity.ok(PRODUCTS);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('product:read')")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable int id) {
        return PRODUCTS.stream()
                .filter(p -> p.get("id").equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:write')")
    public ResponseEntity<Map<String, String>> createProduct(@RequestBody Map<String, Object> product) {
        return ResponseEntity.status(201).body(Map.of(
                "message", "Product created successfully",
                "name", (String) product.get("name")
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable int id) {
        return ResponseEntity.ok(Map.of("message", "Product " + id + " deleted successfully"));
    }
}
