package com.runmarket.api.adapter.out.persistence;

import com.runmarket.api.adapter.out.persistence.entity.RaceLikeJpaEntity;
import com.runmarket.api.adapter.out.persistence.repository.RaceLikeJpaRepository;
import com.runmarket.api.domain.port.out.race.RaceLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RaceLikePersistenceAdapter implements RaceLikeRepository {

    private final RaceLikeJpaRepository raceLikeJpaRepository;

    @Override
    public void save(UUID userId, UUID raceId) {
        RaceLikeJpaEntity entity = RaceLikeJpaEntity.builder()
                .userId(userId)
                .raceId(raceId)
                .build();
        raceLikeJpaRepository.save(entity);
    }

    @Override
    public void delete(UUID userId, UUID raceId) {
        raceLikeJpaRepository.deleteByUserIdAndRaceId(userId, raceId);
    }

    @Override
    public boolean existsByUserIdAndRaceId(UUID userId, UUID raceId) {
        return raceLikeJpaRepository.existsByUserIdAndRaceId(userId, raceId);
    }

    @Override
    public List<UUID> findRaceIdsByUserId(UUID userId) {
        return raceLikeJpaRepository.findRaceIdsByUserId(userId);
    }

    @Override
    public long countByRaceId(UUID raceId) {
        return raceLikeJpaRepository.countByRaceId(raceId);
    }

    @Override
    public Map<UUID, Long> countByRaceIds(List<UUID> raceIds) {
        if (raceIds.isEmpty()) {
            return Map.of();
        }
        List<Object[]> rows = raceLikeJpaRepository.countGroupByRaceIdIn(raceIds);
        Map<UUID, Long> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put((UUID) row[0], (Long) row[1]);
        }
        return result;
    }

    @Override
    public Set<UUID> findLikedRaceIds(UUID userId, List<UUID> raceIds) {
        if (raceIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(raceLikeJpaRepository.findRaceIdsByUserIdAndRaceIdIn(userId, raceIds));
    }
}
