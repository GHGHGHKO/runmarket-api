package com.runmarket.api.adapter.out.persistence.repository;

import com.runmarket.api.adapter.out.persistence.entity.UserJpaEntity;
import com.runmarket.api.domain.model.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
