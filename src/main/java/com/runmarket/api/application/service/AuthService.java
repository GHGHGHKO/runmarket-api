package com.runmarket.api.application.service;

import com.runmarket.api.domain.model.User;
import com.runmarket.api.domain.port.in.auth.AuthToken;
import com.runmarket.api.domain.port.in.auth.LoginCommand;
import com.runmarket.api.domain.port.in.auth.LoginUseCase;
import com.runmarket.api.domain.port.out.auth.TokenProvider;
import com.runmarket.api.domain.port.out.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthToken login(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return tokenProvider.generateToken(user);
    }
}
