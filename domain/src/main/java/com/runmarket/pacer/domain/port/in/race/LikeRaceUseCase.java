package com.runmarket.pacer.domain.port.in.race;

import java.util.UUID;

public interface LikeRaceUseCase {
    void like(UUID raceId, String userEmail);
}
