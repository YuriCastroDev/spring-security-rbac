package com.zs.spring_security_rbac.auth;

import com.zs.spring_security_rbac.dto.AuthResponse;
import com.zs.spring_security_rbac.dto.LoginRequest;
import com.zs.spring_security_rbac.dto.RegisterRequest;
import com.zs.spring_security_rbac.entity.Role;
import com.zs.spring_security_rbac.entity.User;
import com.zs.spring_security_rbac.jwt.JwtService;
import com.zs.spring_security_rbac.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    @Test
    void shouldRegisterUserWithDefaultRoleUser() {
        RegisterRequest request = new RegisterRequest("João", "joao@email.com", "123456", null);

        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("token");

        AuthResponse response = authService.register(request);

        assertThat(response.role()).isEqualTo("ROLE_USER");
        assertThat(response.accessToken()).isEqualTo("token");
    }

    @Test
    void shouldRegisterUserWithSpecifiedRole() {
        RegisterRequest request = new RegisterRequest("Admin", "admin@email.com", "123456", "ADMIN");

        when(userRepository.existsByEmail("admin@email.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("token");

        AuthResponse response = authService.register(request);

        assertThat(response.role()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldThrowWhenEmailAlreadyRegistered() {
        RegisterRequest request = new RegisterRequest("João", "joao@email.com", "123456", null);
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void shouldLoginAndReturnPermissionsList() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("joao@email.com")
                .role(Role.ROLE_VIEWER)
                .build();

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("token");

        AuthResponse response = authService.login(new LoginRequest("joao@email.com", "123456"));

        assertThat(response.permissions()).containsExactlyInAnyOrder(
                "user:read", "product:read", "order:read"
        );
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
