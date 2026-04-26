package com.runmarket.api.domain.port.in.race;

import java.util.UUID;

public interface LikeRaceUseCase {
    void like(UUID raceId, String userEmail);
}
