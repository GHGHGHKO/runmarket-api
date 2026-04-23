package com.runmarket.api.adapter.in.web;

import com.runmarket.api.adapter.in.web.dto.LoginRequest;
import com.runmarket.api.adapter.in.web.dto.TokenResponse;
import com.runmarket.api.adapter.in.web.mapper.TokenResponseMapper;
import com.runmarket.api.domain.port.in.auth.AuthToken;
import com.runmarket.api.domain.port.in.auth.LoginCommand;
import com.runmarket.api.domain.port.in.auth.LoginUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final TokenResponseMapper tokenResponseMapper;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthToken authToken = loginUseCase.login(new LoginCommand(request.email(), request.password()));
        return ResponseEntity.ok(tokenResponseMapper.toResponse(authToken));
    }
}
