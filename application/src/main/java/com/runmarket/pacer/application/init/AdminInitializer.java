package com.runmarket.pacer.application.init;

import com.runmarket.pacer.domain.model.AuthProvider;
import com.runmarket.pacer.domain.model.Role;
import com.runmarket.pacer.domain.model.RoleType;
import com.runmarket.pacer.domain.model.User;
import com.runmarket.pacer.domain.port.out.security.PasswordHashPort;
import com.runmarket.pacer.domain.port.out.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordHashPort passwordHashPort;

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
                .password(passwordHashPort.encode(adminPassword))
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
