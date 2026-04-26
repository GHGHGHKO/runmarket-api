package com.runmarket.api.domain.port.in.auth;

public interface VerifyEmailUseCase {
    void verify(String token);
}
