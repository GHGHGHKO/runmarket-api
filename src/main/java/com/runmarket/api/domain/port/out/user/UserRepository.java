package com.runmarket.api.domain.port.out.user;

import com.runmarket.api.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    User save(User user);
    void updateVerified(UUID userId);
    void deleteById(UUID userId);
}
