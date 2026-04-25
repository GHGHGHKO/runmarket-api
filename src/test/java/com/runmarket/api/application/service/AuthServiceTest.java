package com.runmarket.api.application.service;

import com.runmarket.api.domain.exception.UserNotVerifiedException;
import com.runmarket.api.domain.model.AuthProvider;
import com.runmarket.api.domain.model.User;
import com.runmarket.api.domain.port.in.auth.AuthToken;
import com.runmarket.api.domain.port.in.auth.LoginCommand;
import com.runmarket.api.domain.port.out.auth.TokenProvider;
import com.runmarket.api.domain.port.out.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private TokenProvider tokenProvider;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private AuthService authService;

    private User verifiedUser;
    private User unverifiedUser;

    @BeforeEach
    void setUp() {
        verifiedUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .password("encodedPassword")
                .nickname("test")
                .provider(AuthProvider.EMAIL)
                .verified(true)
                .roles(List.of())
                .build();

        unverifiedUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .password("encodedPassword")
                .nickname("test")
                .provider(AuthProvider.EMAIL)
                .verified(false)
                .roles(List.of())
                .build();
    }

    @Test
    void 로그인_성공() {
        AuthToken expected = new AuthToken("jwt-token", LocalDateTime.now().plusDays(1));
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(verifiedUser));
        given(passwordEncoder.matches("password", "encodedPassword")).willReturn(true);
        given(tokenProvider.generateToken(verifiedUser)).willReturn(expected);

        AuthToken result = authService.login(new LoginCommand("test@test.com", "password"));

        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.expiresAt()).isEqualTo(expected.expiresAt());
    }

    @Test
    void 로그인_존재하지않는이메일_예외() {
        given(userRepository.findByEmail("none@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginCommand("none@test.com", "password")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void 로그인_비밀번호불일치_예외() {
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(verifiedUser));
        given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginCommand("test@test.com", "wrongPassword")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void 로그인_미인증유저_예외() {
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(unverifiedUser));
        given(passwordEncoder.matches("password", "encodedPassword")).willReturn(true);

        assertThatThrownBy(() -> authService.login(new LoginCommand("test@test.com", "password")))
                .isInstanceOf(UserNotVerifiedException.class);
    }
}
