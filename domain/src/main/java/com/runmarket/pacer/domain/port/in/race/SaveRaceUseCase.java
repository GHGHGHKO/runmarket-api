package com.runmarket.pacer.domain.port.in.race;

import com.runmarket.pacer.domain.model.Race;

public interface SaveRaceUseCase {
    Race save(SaveRaceCommand command);
}
