package com.runmarket.pacer.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TokenResponse {
    private final String accessToken;
    private final LocalDateTime expiresAt;
}
