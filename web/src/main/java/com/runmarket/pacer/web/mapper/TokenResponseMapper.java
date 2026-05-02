package com.runmarket.pacer.web.mapper;

import com.runmarket.pacer.domain.port.in.auth.AuthToken;
import com.runmarket.pacer.web.dto.TokenResponse;
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
