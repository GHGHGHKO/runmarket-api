package com.runmarket.api.adapter.in.web;

import com.runmarket.api.adapter.in.web.dto.RaceListItemResponse;
import com.runmarket.api.common.SecurityUtils;
import com.runmarket.api.domain.model.Race;
import com.runmarket.api.domain.port.in.race.GetLikedRacesUseCase;
import com.runmarket.api.domain.port.in.race.GetRaceLikeCountUseCase;
import com.runmarket.api.domain.port.in.user.WithdrawUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final WithdrawUseCase withdrawUseCase;
    private final GetLikedRacesUseCase getLikedRacesUseCase;
    private final GetRaceLikeCountUseCase getRaceLikeCountUseCase;

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw() {
        withdrawUseCase.withdraw(SecurityUtils.currentUserEmail());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/liked-races")
    public ResponseEntity<List<RaceListItemResponse>> getLikedRaces() {
        List<Race> races = getLikedRacesUseCase.getLikedRaces(SecurityUtils.currentUserEmail());
        List<UUID> raceIds = races.stream().map(Race::getId).toList();
        Map<UUID, Long> likeCounts = getRaceLikeCountUseCase.getLikeCounts(raceIds);
        return ResponseEntity.ok(races.stream()
                .map(race -> RaceListItemResponse.from(race, likeCounts.getOrDefault(race.getId(), 0L)))
                .toList());
    }
}
