package com.runmarket.pacer.infrastructure.persistence.repository;

import com.runmarket.pacer.infrastructure.persistence.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, UUID> {
    List<RoleJpaEntity> findAllByUserId(UUID userId);
    void deleteAllByUserId(UUID userId);
}
