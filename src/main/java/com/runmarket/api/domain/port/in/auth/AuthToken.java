package com.runmarket.api.domain.port.in.auth;

import java.time.LocalDateTime;

public record AuthToken(String token, LocalDateTime expiresAt) {}
