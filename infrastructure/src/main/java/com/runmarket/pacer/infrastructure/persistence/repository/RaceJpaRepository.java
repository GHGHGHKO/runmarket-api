package com.runmarket.pacer.infrastructure.persistence.repository;

import com.runmarket.pacer.infrastructure.persistence.entity.RaceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RaceJpaRepository extends JpaRepository<RaceJpaEntity, UUID> {
    Optional<RaceJpaEntity> findByExternalId(Integer externalId);
}
