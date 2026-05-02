package com.runmarket.pacer.web.controller;

import com.runmarket.pacer.domain.port.in.auth.AuthToken;
import com.runmarket.pacer.domain.port.in.auth.LoginCommand;
import com.runmarket.pacer.domain.port.in.auth.LoginUseCase;
import com.runmarket.pacer.domain.port.in.auth.RegisterCommand;
import com.runmarket.pacer.domain.port.in.auth.RegisterUseCase;
import com.runmarket.pacer.domain.port.in.auth.VerifyEmailUseCase;
import com.runmarket.pacer.web.dto.LoginRequest;
import com.runmarket.pacer.web.dto.RegisterRequest;
import com.runmarket.pacer.web.dto.RegisterResponse;
import com.runmarket.pacer.web.dto.TokenResponse;
import com.runmarket.pacer.web.dto.VerifyEmailRequest;
import com.runmarket.pacer.web.mapper.TokenResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final TokenResponseMapper tokenResponseMapper;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthToken authToken = loginUseCase.login(
                new LoginCommand(request.email(), request.password(), request.turnstileToken()));
        return ResponseEntity.ok(tokenResponseMapper.toResponse(authToken));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        registerUseCase.register(
                new RegisterCommand(request.email(), request.password(), request.turnstileToken()));
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new RegisterResponse("인증 이메일을 발송했습니다. 이메일을 확인하여 회원가입을 완료해주세요."));
    }

    @PatchMapping("/verify")
    public ResponseEntity<RegisterResponse> verify(@Valid @RequestBody VerifyEmailRequest request) {
        verifyEmailUseCase.verify(request.token());
        return ResponseEntity.ok(new RegisterResponse("이메일 인증이 완료되었습니다. 로그인하실 수 있습니다."));
    }
}
