package com.runmarket.api.domain.port.in.race;

import com.runmarket.api.domain.model.Race;

import java.util.List;

public interface GetLikedRacesUseCase {
    List<Race> getLikedRaces(String userEmail);
}
