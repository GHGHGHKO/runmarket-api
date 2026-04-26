package com.runmarket.api.adapter.out.persistence.repository;

import com.runmarket.api.adapter.out.persistence.entity.EmailVerificationTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenJpaRepository extends JpaRepository<EmailVerificationTokenJpaEntity, UUID> {
    Optional<EmailVerificationTokenJpaEntity> findByToken(String token);
    void deleteByUserId(UUID userId);
}
