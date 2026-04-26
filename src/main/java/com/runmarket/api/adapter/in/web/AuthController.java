package com.runmarket.api.adapter.in.web;

import com.runmarket.api.adapter.in.web.dto.LoginRequest;
import com.runmarket.api.adapter.in.web.dto.RegisterRequest;
import com.runmarket.api.adapter.in.web.dto.RegisterResponse;
import com.runmarket.api.adapter.in.web.dto.TokenResponse;
import com.runmarket.api.adapter.in.web.dto.VerifyEmailRequest;
import com.runmarket.api.adapter.in.web.mapper.TokenResponseMapper;
import com.runmarket.api.domain.port.in.auth.AuthToken;
import com.runmarket.api.domain.port.in.auth.LoginCommand;
import com.runmarket.api.domain.port.in.auth.LoginUseCase;
import com.runmarket.api.domain.port.in.auth.RegisterCommand;
import com.runmarket.api.domain.port.in.auth.RegisterUseCase;
import com.runmarket.api.domain.port.in.auth.VerifyEmailUseCase;
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
        AuthToken authToken = loginUseCase.login(new LoginCommand(request.email(), request.password()));
        return ResponseEntity.ok(tokenResponseMapper.toResponse(authToken));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        registerUseCase.register(new RegisterCommand(request.email(), request.password()));
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new RegisterResponse("인증 이메일을 발송했습니다. 이메일을 확인하여 회원가입을 완료해주세요."));
    }

    @PatchMapping("/verify")
    public ResponseEntity<RegisterResponse> verify(@Valid @RequestBody VerifyEmailRequest request) {
        verifyEmailUseCase.verify(request.token());
        return ResponseEntity.ok(new RegisterResponse("이메일 인증이 완료되었습니다. 로그인하실 수 있습니다."));
    }
}
