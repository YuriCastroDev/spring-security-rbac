package com.zs.spring_security_rbac.auth;

import com.zs.spring_security_rbac.dto.AuthResponse;
import com.zs.spring_security_rbac.dto.LoginRequest;
import com.zs.spring_security_rbac.dto.RegisterRequest;
import com.zs.spring_security_rbac.entity.Role;
import com.zs.spring_security_rbac.entity.User;
import com.zs.spring_security_rbac.jwt.JwtService;
import com.zs.spring_security_rbac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered: " + request.email());
        }

        Role role = parseRole(request.role());

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();

        userRepository.save(user);
        log.info("User registered: {} with role: {}", user.getEmail(), role);

        return buildResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        log.info("User logged in: {} with role: {}", user.getEmail(), user.getRole());
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        String token = jwtService.generateToken(user);
        List<String> permissions = user.getRole().getPermissions()
                .stream()
                .map(p -> p.getPermission())
                .toList();

        return AuthResponse.of(token, user.getEmail(), user.getRole().name(), permissions);
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) return Role.ROLE_USER;
        try {
            return Role.valueOf(role.toUpperCase().startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.ROLE_USER;
        }
    }
}
