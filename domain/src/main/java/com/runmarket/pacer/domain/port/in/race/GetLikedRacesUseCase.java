package com.runmarket.pacer.domain.port.in.race;

import com.runmarket.pacer.domain.model.Race;

import java.util.List;

public interface GetLikedRacesUseCase {
    List<Race> getLikedRaces(String userEmail);
}
