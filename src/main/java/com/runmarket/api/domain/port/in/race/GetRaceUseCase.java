package com.runmarket.api.domain.port.in.race;

import com.runmarket.api.domain.model.Race;

import java.util.UUID;

public interface GetRaceUseCase {
    Race getRace(UUID id);
}
