package com.runmarket.pacer.domain.port.out.auth;

import com.runmarket.pacer.domain.model.User;
import com.runmarket.pacer.domain.port.in.auth.AuthToken;

public interface TokenProvider {
    AuthToken generateToken(User user);
    boolean validateToken(String token);
    String getSubject(String token);
}
