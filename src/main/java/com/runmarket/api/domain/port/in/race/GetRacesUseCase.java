package com.runmarket.api.domain.port.in.race;

import com.runmarket.api.domain.model.Race;

import java.util.List;

public interface GetRacesUseCase {
    List<Race> getRaces();
}
