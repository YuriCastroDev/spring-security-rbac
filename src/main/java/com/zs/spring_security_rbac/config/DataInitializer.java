package com.zs.spring_security_rbac.config;

import com.zs.spring_security_rbac.entity.Role;
import com.zs.spring_security_rbac.entity.User;
import com.zs.spring_security_rbac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            createUser("Admin User", "admin@example.com", Role.ROLE_ADMIN);
            createUser("Moderator User", "moderator@example.com", Role.ROLE_MODERATOR);
            createUser("Regular User", "user@example.com", Role.ROLE_USER);
            createUser("Viewer User", "viewer@example.com", Role.ROLE_VIEWER);
            log.info("Default users created — password for all: 'password123'");
        }
    }

    private void createUser(String name, String email, Role role) {
        userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode("password123"))
                .role(role)
                .build());
    }
}
