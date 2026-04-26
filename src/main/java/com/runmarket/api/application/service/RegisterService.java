package com.runmarket.api.application.service;

import com.runmarket.api.domain.event.EmailVerificationEvent;
import com.runmarket.api.domain.exception.EmailAlreadyExistsException;
import com.runmarket.api.domain.exception.InvalidVerificationTokenException;
import com.runmarket.api.domain.model.AuthProvider;
import com.runmarket.api.domain.model.EmailVerificationToken;
import com.runmarket.api.domain.model.Role;
import com.runmarket.api.domain.model.RoleType;
import com.runmarket.api.domain.model.User;
import com.runmarket.api.domain.port.in.auth.RegisterCommand;
import com.runmarket.api.domain.port.in.auth.RegisterUseCase;
import com.runmarket.api.domain.port.in.auth.VerifyEmailUseCase;
import com.runmarket.api.domain.port.out.user.EmailVerificationTokenRepository;
import com.runmarket.api.domain.port.out.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterUseCase, VerifyEmailUseCase {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.verification.token-expiry-minutes:30}")
    private int tokenExpiryMinutes;

    @Override
    @Transactional
    public void register(RegisterCommand command) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new EmailAlreadyExistsException(command.email());
        }

        User user = userRepository.save(User.builder()
                .email(command.email())
                .password(passwordEncoder.encode(command.password()))
                .nickname(command.email())
                .provider(AuthProvider.EMAIL)
                .verified(false)
                .roles(List.of(Role.builder()
                        .roleType(RoleType.ROLE_USER)
                        .build()))
                .build());

        String rawToken = UUID.randomUUID().toString();
        tokenRepository.save(EmailVerificationToken.builder()
                .userId(user.getId())
                .token(rawToken)
                .expiresAt(LocalDateTime.now().plusMinutes(tokenExpiryMinutes))
                .build());

        String verificationLink = baseUrl + "/verify?token=" + rawToken;
        eventPublisher.publishEvent(new EmailVerificationEvent(command.email(), verificationLink));
        log.info("User registered: email={}", command.email());
    }

    @Override
    @Transactional
    public void verify(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(InvalidVerificationTokenException::new);

        if (verificationToken.isExpired()) {
            throw new InvalidVerificationTokenException();
        }

        userRepository.updateVerified(verificationToken.getUserId());
        tokenRepository.deleteByUserId(verificationToken.getUserId());
        log.info("Email verified: userId={}", verificationToken.getUserId());
    }
}
