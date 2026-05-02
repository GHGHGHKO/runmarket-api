package com.runmarket.pacer.domain.port.out.user;

import com.runmarket.pacer.domain.model.EmailVerificationToken;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository {
    EmailVerificationToken save(EmailVerificationToken token);
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteByUserId(UUID userId);
}
