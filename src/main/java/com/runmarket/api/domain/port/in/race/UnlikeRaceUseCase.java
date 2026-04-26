package com.runmarket.api.domain.port.in.race;

import java.util.UUID;

public interface UnlikeRaceUseCase {
    void unlike(UUID raceId, String userEmail);
}
