package com.runmarket.api.application.service;

import com.runmarket.api.domain.event.EmailVerificationEvent;
import com.runmarket.api.domain.exception.EmailAlreadyExistsException;
import com.runmarket.api.domain.exception.InvalidVerificationTokenException;
import com.runmarket.api.domain.model.AuthProvider;
import com.runmarket.api.domain.model.EmailVerificationToken;
import com.runmarket.api.domain.model.User;
import com.runmarket.api.domain.port.in.auth.RegisterCommand;
import com.runmarket.api.domain.port.out.user.EmailVerificationTokenRepository;
import com.runmarket.api.domain.port.out.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private EmailVerificationTokenRepository tokenRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private RegisterService registerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(registerService, "baseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(registerService, "tokenExpiryMinutes", 30);
    }

    @Test
    void 회원가입_성공_이벤트발행() {
        UUID userId = UUID.randomUUID();
        User savedUser = User.builder()
                .id(userId)
                .email("new@test.com")
                .password("encoded")
                .nickname("new@test.com")
                .provider(AuthProvider.EMAIL)
                .verified(false)
                .roles(List.of())
                .build();
        given(userRepository.findByEmail("new@test.com")).willReturn(Optional.empty());
        given(passwordEncoder.encode("password")).willReturn("encoded");
        given(userRepository.save(any())).willReturn(savedUser);
        given(tokenRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        registerService.register(new RegisterCommand("new@test.com", "password"));

        ArgumentCaptor<EmailVerificationEvent> captor = ArgumentCaptor.forClass(EmailVerificationEvent.class);
        then(eventPublisher).should().publishEvent(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("new@test.com");
        assertThat(captor.getValue().verificationLink()).contains("/verify?token=");
    }

    @Test
    void 회원가입_중복이메일_예외() {
        User existing = User.builder().email("dup@test.com").roles(List.of()).build();
        given(userRepository.findByEmail("dup@test.com")).willReturn(Optional.of(existing));

        assertThatThrownBy(() -> registerService.register(new RegisterCommand("dup@test.com", "password")))
                .isInstanceOf(EmailAlreadyExistsException.class);

        then(eventPublisher).shouldHaveNoInteractions();
    }

    @Test
    void 이메일인증_성공() {
        UUID userId = UUID.randomUUID();
        EmailVerificationToken token = EmailVerificationToken.builder()
                .userId(userId)
                .token("valid-token")
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
        given(tokenRepository.findByToken("valid-token")).willReturn(Optional.of(token));

        registerService.verify("valid-token");

        then(userRepository).should().updateVerified(userId);
        then(tokenRepository).should().deleteByUserId(userId);
    }

    @Test
    void 이메일인증_존재하지않는토큰_예외() {
        given(tokenRepository.findByToken("invalid-token")).willReturn(Optional.empty());

        assertThatThrownBy(() -> registerService.verify("invalid-token"))
                .isInstanceOf(InvalidVerificationTokenException.class);
    }

    @Test
    void 이메일인증_만료된토큰_예외() {
        EmailVerificationToken expiredToken = EmailVerificationToken.builder()
                .userId(UUID.randomUUID())
                .token("expired-token")
                .expiresAt(LocalDateTime.now().minusSeconds(1))
                .build();
        given(tokenRepository.findByToken("expired-token")).willReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> registerService.verify("expired-token"))
                .isInstanceOf(InvalidVerificationTokenException.class);
    }
}
