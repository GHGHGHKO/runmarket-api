package com.runmarket.pacer.domain.port.in.auth;

public interface VerifyEmailUseCase {
    void verify(String token);
}
