package com.runmarket.api.domain.port.in.auth;

public interface LoginUseCase {
    AuthToken login(LoginCommand command);
}
