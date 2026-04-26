package com.runmarket.api.application.service;

import com.runmarket.api.domain.model.Race;
import com.runmarket.api.domain.model.User;
import com.runmarket.api.domain.port.in.race.GetLikedRacesUseCase;
import com.runmarket.api.domain.port.in.race.GetRaceLikeCountUseCase;
import com.runmarket.api.domain.port.in.race.GetRaceLikeStatusUseCase;
import com.runmarket.api.domain.port.in.race.LikeRaceUseCase;
import com.runmarket.api.domain.port.in.race.UnlikeRaceUseCase;
import com.runmarket.api.domain.port.out.race.RaceLikeRepository;
import com.runmarket.api.domain.port.out.race.RaceRepository;
import com.runmarket.api.domain.port.out.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RaceLikeService implements LikeRaceUseCase, UnlikeRaceUseCase, GetLikedRacesUseCase, GetRaceLikeCountUseCase, GetRaceLikeStatusUseCase {

    private final RaceLikeRepository raceLikeRepository;
    private final RaceRepository raceRepository;
    private final UserRepository userRepository;

    @Override
    public void like(UUID raceId, String userEmail) {
        User user = getUser(userEmail);
        raceRepository.findById(raceId)
                .orElseThrow(() -> new NoSuchElementException("대회를 찾을 수 없습니다: " + raceId));

        if (raceLikeRepository.existsByUserIdAndRaceId(user.getId(), raceId)) {
            return;
        }
        raceLikeRepository.save(user.getId(), raceId);
        log.info("Race liked: raceId={}, userId={}", raceId, user.getId());
    }

    @Override
    public void unlike(UUID raceId, String userEmail) {
        User user = getUser(userEmail);
        if (!raceLikeRepository.existsByUserIdAndRaceId(user.getId(), raceId)) {
            return;
        }
        raceLikeRepository.delete(user.getId(), raceId);
        log.info("Race unliked: raceId={}, userId={}", raceId, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Race> getLikedRaces(String userEmail) {
        User user = getUser(userEmail);
        List<UUID> raceIds = raceLikeRepository.findRaceIdsByUserId(user.getId());
        return raceRepository.findAllByIds(raceIds);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCount(UUID raceId) {
        return raceLikeRepository.countByRaceId(raceId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, Long> getLikeCounts(List<UUID> raceIds) {
        return raceLikeRepository.countByRaceIds(raceIds);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(UUID raceId, String userEmail) {
        if (isAnonymous(userEmail)) {
            return false;
        }
        return userRepository.findByEmail(userEmail)
                .map(user -> raceLikeRepository.existsByUserIdAndRaceId(user.getId(), raceId))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> getLikedRaceIds(List<UUID> raceIds, String userEmail) {
        if (isAnonymous(userEmail) || raceIds.isEmpty()) {
            return Set.of();
        }
        return userRepository.findByEmail(userEmail)
                .map(user -> raceLikeRepository.findLikedRaceIds(user.getId(), raceIds))
                .orElse(Set.of());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    }

    private boolean isAnonymous(String userEmail) {
        return "anonymous".equals(userEmail);
    }
}
