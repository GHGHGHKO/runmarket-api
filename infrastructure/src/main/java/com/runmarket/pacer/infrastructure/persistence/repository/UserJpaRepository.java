package com.runmarket.pacer.infrastructure.persistence.repository;

import com.runmarket.pacer.domain.model.AuthProvider;
import com.runmarket.pacer.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
