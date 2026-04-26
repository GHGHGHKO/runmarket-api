package com.runmarket.api.domain.port.out.race;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface RaceLikeRepository {
    void save(UUID userId, UUID raceId);
    void delete(UUID userId, UUID raceId);
    boolean existsByUserIdAndRaceId(UUID userId, UUID raceId);
    List<UUID> findRaceIdsByUserId(UUID userId);
    long countByRaceId(UUID raceId);
    Map<UUID, Long> countByRaceIds(List<UUID> raceIds);
    Set<UUID> findLikedRaceIds(UUID userId, List<UUID> raceIds);
}
