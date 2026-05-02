package com.runmarket.pacer.domain.port.in.race;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GetRaceLikeCountUseCase {
    long getLikeCount(UUID raceId);
    Map<UUID, Long> getLikeCounts(List<UUID> raceIds);
}
