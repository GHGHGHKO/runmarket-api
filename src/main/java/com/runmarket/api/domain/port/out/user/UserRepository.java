package com.runmarket.api.domain.port.out.user;

import com.runmarket.api.domain.model.AuthProvider;
import com.runmarket.api.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
    User save(User user);
}
