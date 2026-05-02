package com.runmarket.pacer.domain.port.in.race;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GetRaceLikeStatusUseCase {
    boolean isLiked(UUID raceId, String userEmail);
    Set<UUID> getLikedRaceIds(List<UUID> raceIds, String userEmail);
}
