package com.runmarket.api.adapter.out.persistence.repository;

import com.runmarket.api.adapter.out.persistence.entity.RaceLikeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RaceLikeJpaRepository extends JpaRepository<RaceLikeJpaEntity, UUID> {

    boolean existsByUserIdAndRaceId(UUID userId, UUID raceId);

    void deleteByUserIdAndRaceId(UUID userId, UUID raceId);

    @Query("SELECT rl.raceId FROM RaceLikeJpaEntity rl WHERE rl.userId = :userId")
    List<UUID> findRaceIdsByUserId(@Param("userId") UUID userId);

    long countByRaceId(UUID raceId);

    @Query("SELECT rl.raceId, COUNT(rl) FROM RaceLikeJpaEntity rl WHERE rl.raceId IN :raceIds GROUP BY rl.raceId")
    List<Object[]> countGroupByRaceIdIn(@Param("raceIds") List<UUID> raceIds);

    @Query("SELECT rl.raceId FROM RaceLikeJpaEntity rl WHERE rl.userId = :userId AND rl.raceId IN :raceIds")
    List<UUID> findRaceIdsByUserIdAndRaceIdIn(@Param("userId") UUID userId, @Param("raceIds") List<UUID> raceIds);
}
