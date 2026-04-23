package com.runmarket.api.domain.port.out.auth;

import com.runmarket.api.domain.model.User;
import com.runmarket.api.domain.port.in.auth.AuthToken;

public interface TokenProvider {
    AuthToken generateToken(User user);
    boolean validateToken(String token);
    String getSubject(String token);
}
