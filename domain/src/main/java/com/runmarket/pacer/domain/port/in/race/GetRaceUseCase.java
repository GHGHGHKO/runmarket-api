package com.runmarket.pacer.domain.port.in.race;

import com.runmarket.pacer.domain.model.Race;

import java.util.UUID;

public interface GetRaceUseCase {
    Race getRace(UUID id);
}
