package com.runmarket.pacer.domain.port.in.auth;

public interface LoginUseCase {
    AuthToken login(LoginCommand command);
}
