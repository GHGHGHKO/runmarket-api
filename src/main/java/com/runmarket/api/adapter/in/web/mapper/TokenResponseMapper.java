package com.runmarket.api.adapter.in.web.mapper;

import com.runmarket.api.adapter.in.web.dto.TokenResponse;
import com.runmarket.api.domain.port.in.auth.AuthToken;
import org.springframework.stereotype.Component;

@Component
public class TokenResponseMapper {

    public TokenResponse toResponse(AuthToken authToken) {
        return TokenResponse.builder()
                .accessToken(authToken.token())
                .expiresAt(authToken.expiresAt())
                .build();
    }
}
