package com.zs.spring_security_rbac.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditFilter extends OncePerRequestFilter {

    private final AuditLogRepository auditLogRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        filterChain.doFilter(request, response);

        // Só audita rotas da API (ignora assets estáticos)
        if (!request.getRequestURI().startsWith("/api/"))
            return;

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser"))
                    ? auth.getName()
                    : "anonymous";
            String role = (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser"))
                    ? auth.getAuthorities().stream()
                            .filter(a -> a.getAuthority().startsWith("ROLE_"))
                            .map(a -> a.getAuthority())
                            .findFirst().orElse("NONE")
                    : "NONE";

            AuditLog log = AuditLog.builder()
                    .userEmail(email)
                    .userRole(role)
                    .httpMethod(request.getMethod())
                    .endpoint(request.getRequestURI())
                    .statusCode(response.getStatus())
                    .ipAddress(request.getRemoteAddr())
                    .build();

            auditLogRepository.save(log);
        } catch (Exception e) {
            log.warn("Failed to save audit log: {}", e.getMessage());
        }
    }
}
