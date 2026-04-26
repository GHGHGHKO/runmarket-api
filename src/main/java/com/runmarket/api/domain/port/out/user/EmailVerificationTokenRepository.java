package com.runmarket.api.domain.port.out.user;

import com.runmarket.api.domain.model.EmailVerificationToken;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository {
    EmailVerificationToken save(EmailVerificationToken token);
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteByUserId(UUID userId);
}
