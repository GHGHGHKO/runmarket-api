package com.runmarket.pacer.web.controller;

import com.runmarket.pacer.domain.model.Race;
import com.runmarket.pacer.domain.port.in.race.GetLikedRacesUseCase;
import com.runmarket.pacer.domain.port.in.race.GetRaceLikeCountUseCase;
import com.runmarket.pacer.domain.port.in.race.GetRaceLikeStatusUseCase;
import com.runmarket.pacer.domain.port.in.race.GetRaceUseCase;
import com.runmarket.pacer.domain.port.in.race.GetRacesUseCase;
import com.runmarket.pacer.domain.port.in.race.LikeRaceUseCase;
import com.runmarket.pacer.domain.port.in.race.SaveRaceCommand;
import com.runmarket.pacer.domain.port.in.race.SaveRaceUseCase;
import com.runmarket.pacer.domain.port.in.race.UnlikeRaceUseCase;
import com.runmarket.pacer.web.dto.RaceDetailResponse;
import com.runmarket.pacer.web.dto.RaceLikeResponse;
import com.runmarket.pacer.web.dto.RaceListItemResponse;
import com.runmarket.pacer.web.dto.SaveRaceRequest;
import com.runmarket.pacer.web.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/races")
@RequiredArgsConstructor
public class RaceController {

    private final SaveRaceUseCase saveRaceUseCase;
    private final GetRacesUseCase getRacesUseCase;
    private final GetRaceUseCase getRaceUseCase;
    private final LikeRaceUseCase likeRaceUseCase;
    private final UnlikeRaceUseCase unlikeRaceUseCase;
    private final GetRaceLikeCountUseCase getRaceLikeCountUseCase;
    private final GetRaceLikeStatusUseCase getRaceLikeStatusUseCase;

    @PutMapping("/{externalId}")
    public ResponseEntity<RaceDetailResponse> saveRace(
            @PathVariable Integer externalId,
            @Valid @RequestBody SaveRaceRequest request) {
        Race saved = saveRaceUseCase.save(new SaveRaceCommand(
                externalId, request.name(), request.courses(),
                request.date(), request.startTime(),
                request.venue(), request.venueAddress(), request.region(),
                request.organizer(), request.representative(), request.phone(), request.email(),
                request.registrationStartDate(), request.registrationEndDate(),
                request.homepageUrl(), request.lat(), request.lng(), request.description()
        ));
        String userEmail = SecurityUtils.currentUserEmail();
        long likeCount = getRaceLikeCountUseCase.getLikeCount(saved.getId());
        boolean isLiked = getRaceLikeStatusUseCase.isLiked(saved.getId(), userEmail);
        return ResponseEntity.ok(RaceDetailResponse.from(saved, likeCount, isLiked));
    }

    @GetMapping
    public ResponseEntity<List<RaceListItemResponse>> getRaces() {
        List<Race> races = getRacesUseCase.getRaces();
        List<UUID> raceIds = races.stream().map(Race::getId).toList();
        String userEmail = SecurityUtils.currentUserEmail();
        Map<UUID, Long> likeCounts = getRaceLikeCountUseCase.getLikeCounts(raceIds);
        Set<UUID> likedRaceIds = getRaceLikeStatusUseCase.getLikedRaceIds(raceIds, userEmail);
        return ResponseEntity.ok(races.stream()
                .map(race -> RaceListItemResponse.from(
                        race,
                        likeCounts.getOrDefault(race.getId(), 0L),
                        likedRaceIds.contains(race.getId())))
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaceDetailResponse> getRace(@PathVariable UUID id) {
        Race race = getRaceUseCase.getRace(id);
        String userEmail = SecurityUtils.currentUserEmail();
        long likeCount = getRaceLikeCountUseCase.getLikeCount(race.getId());
        boolean isLiked = getRaceLikeStatusUseCase.isLiked(race.getId(), userEmail);
        return ResponseEntity.ok(RaceDetailResponse.from(race, likeCount, isLiked));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<RaceLikeResponse> likeRace(@PathVariable UUID id) {
        likeRaceUseCase.like(id, SecurityUtils.currentUserEmail());
        return ResponseEntity.ok(new RaceLikeResponse(getRaceLikeCountUseCase.getLikeCount(id), true));
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<RaceLikeResponse> unlikeRace(@PathVariable UUID id) {
        unlikeRaceUseCase.unlike(id, SecurityUtils.currentUserEmail());
        return ResponseEntity.ok(new RaceLikeResponse(getRaceLikeCountUseCase.getLikeCount(id), false));
    }
}
