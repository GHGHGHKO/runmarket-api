package com.runmarket.pacer.application.service;

import com.runmarket.pacer.domain.exception.InvalidCredentialsException;
import com.runmarket.pacer.domain.exception.UserNotVerifiedException;
import com.runmarket.pacer.domain.model.User;
import com.runmarket.pacer.domain.port.in.auth.AuthToken;
import com.runmarket.pacer.domain.port.in.auth.LoginCommand;
import com.runmarket.pacer.domain.port.in.auth.LoginUseCase;
import com.runmarket.pacer.domain.port.out.auth.TokenProvider;
import com.runmarket.pacer.domain.port.out.security.PasswordHashPort;
import com.runmarket.pacer.domain.port.out.user.UserRepository;
import com.runmarket.pacer.domain.port.out.verification.CaptchaVerificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class AuthService implements LoginUseCase {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordHashPort passwordHashPort;
    private final CaptchaVerificationPort captchaVerificationPort;

    @Override
    public AuthToken login(LoginCommand command) {
        captchaVerificationPort.verify(command.captchaToken());

        User user = userRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHashPort.matches(command.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isVerified()) {
            throw new UserNotVerifiedException();
        }

        return tokenProvider.generateToken(user);
    }
}
