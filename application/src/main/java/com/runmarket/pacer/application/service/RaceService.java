package com.runmarket.pacer.application.service;

import com.runmarket.pacer.domain.model.Race;
import com.runmarket.pacer.domain.port.in.race.GetRaceUseCase;
import com.runmarket.pacer.domain.port.in.race.GetRacesUseCase;
import com.runmarket.pacer.domain.port.in.race.SaveRaceCommand;
import com.runmarket.pacer.domain.port.in.race.SaveRaceUseCase;
import com.runmarket.pacer.domain.port.out.race.RaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RaceService implements SaveRaceUseCase, GetRacesUseCase, GetRaceUseCase {

    private final RaceRepository raceRepository;

    @Override
    @CacheEvict(cacheNames = "races", allEntries = true)
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
        Race saved = raceRepository.save(race);
        log.info("Race saved: id={}, name={}", saved.getId(), saved.getName());
        return saved;
    }

    @Override
    @Cacheable("races")
    @Transactional(readOnly = true)
    public List<Race> getRaces() {
        return raceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Race getRace(UUID id) {
        return raceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Race not found: " + id));
    }
}
