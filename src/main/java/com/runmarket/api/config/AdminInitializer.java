package com.runmarket.api.config;

import com.runmarket.api.domain.model.AuthProvider;
import com.runmarket.api.domain.model.Role;
import com.runmarket.api.domain.model.RoleType;
import com.runmarket.api.domain.model.User;
import com.runmarket.api.domain.port.out.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminPassword.isBlank()) {
            log.warn("ADMIN_PASSWORD is not set. Skipping admin account creation.");
            return;
        }

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        userRepository.save(User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .nickname(adminEmail)
                .provider(AuthProvider.EMAIL)
                .verified(true)
                .roles(List.of(
                        Role.builder().roleType(RoleType.ROLE_USER).build(),
                        Role.builder().roleType(RoleType.ROLE_ADMIN).build()
                ))
                .build());

        log.info("Admin account created: {}", adminEmail);
    }
}
