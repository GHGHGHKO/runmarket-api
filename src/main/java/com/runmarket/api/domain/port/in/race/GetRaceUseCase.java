package com.runmarket.api.domain.port.in.race;

import com.runmarket.api.domain.model.Race;

public interface GetRaceUseCase {
    Race getRace(Integer externalId);
}
