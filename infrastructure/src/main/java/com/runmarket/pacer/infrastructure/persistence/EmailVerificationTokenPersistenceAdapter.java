package com.runmarket.pacer.infrastructure.persistence;

import com.runmarket.pacer.domain.model.EmailVerificationToken;
import com.runmarket.pacer.domain.port.out.user.EmailVerificationTokenRepository;
import com.runmarket.pacer.infrastructure.persistence.entity.EmailVerificationTokenJpaEntity;
import com.runmarket.pacer.infrastructure.persistence.repository.EmailVerificationTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmailVerificationTokenPersistenceAdapter implements EmailVerificationTokenRepository {

    private final EmailVerificationTokenJpaRepository jpaRepository;

    @Override
    @Transactional
    public EmailVerificationToken save(EmailVerificationToken token) {
        EmailVerificationTokenJpaEntity entity = jpaRepository.save(
                EmailVerificationTokenJpaEntity.builder()
                        .userId(token.getUserId())
                        .token(token.getToken())
                        .expiresAt(token.getExpiresAt())
                        .build()
        );
        return toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailVerificationToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }

    private EmailVerificationToken toDomain(EmailVerificationTokenJpaEntity entity) {
        return EmailVerificationToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .build();
    }
}
