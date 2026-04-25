package com.runmarket.api.application.service;

import com.runmarket.api.domain.model.Race;
import com.runmarket.api.domain.port.in.race.GetRaceUseCase;
import com.runmarket.api.domain.port.in.race.GetRacesUseCase;
import com.runmarket.api.domain.port.in.race.SaveRaceCommand;
import com.runmarket.api.domain.port.in.race.SaveRaceUseCase;
import com.runmarket.api.domain.port.out.race.RaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class RaceService implements SaveRaceUseCase, GetRacesUseCase, GetRaceUseCase {

    private final RaceRepository raceRepository;

    @Override
    public Race save(SaveRaceCommand command) {
        Race race = Race.builder()
                .externalId(command.externalId())
                .name(command.name())
                .courses(command.courses())
                .date(command.date())
                .startTime(command.startTime())
                .venue(command.venue())
                .venueAddress(command.venueAddress())
                .region(command.region())
                .organizer(command.organizer())
                .representative(command.representative())
                .phone(command.phone())
                .email(command.email())
                .registrationStartDate(command.registrationStartDate())
                .registrationEndDate(command.registrationEndDate())
                .homepageUrl(command.homepageUrl())
                .lat(command.lat())
                .lng(command.lng())
                .description(command.description())
                .build();
        return raceRepository.save(race);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Race> getRaces() {
        return raceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Race getRace(Integer externalId) {
        return raceRepository.findByExternalId(externalId)
                .orElseThrow(() -> new NoSuchElementException("Race not found: " + externalId));
    }
}
