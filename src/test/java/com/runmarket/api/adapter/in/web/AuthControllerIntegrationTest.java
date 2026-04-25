package com.runmarket.api.adapter.in.web;

import com.runmarket.api.adapter.out.email.EmailAdapter;
import com.runmarket.api.adapter.out.persistence.repository.EmailVerificationTokenJpaRepository;
import com.runmarket.api.adapter.out.persistence.repository.RoleJpaRepository;
import com.runmarket.api.adapter.out.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    MockMvcTester mvc;

    @MockitoBean EmailAdapter emailAdapter;

    @Autowired UserJpaRepository userJpaRepository;
    @Autowired RoleJpaRepository roleJpaRepository;
    @Autowired EmailVerificationTokenJpaRepository tokenJpaRepository;

    @BeforeEach
    void setUp(@Autowired WebApplicationContext context) {
        mvc = MockMvcTester.from(context, builder ->
                builder.apply(SecurityMockMvcConfigurers.springSecurity()).build());

        tokenJpaRepository.deleteAll();
        roleJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    // ==================== register ====================

    @Test
    void 회원가입_성공_202반환() {
        assertThat(register("user@test.com", "password123"))
                .hasStatus(202)
                .bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    void 회원가입_중복이메일_409반환() {
        register("dup@test.com", "password123");

        assertThat(register("dup@test.com", "password123"))
                .hasStatus(409);
    }

    @Test
    void 회원가입_잘못된이메일형식_400반환() {
        assertThat(register("not-an-email", "password123"))
                .hasStatus(400)
                .bodyJson().extractingPath("$.detail").asString().isEqualTo("Validation failed");
    }

    @Test
    void 회원가입_짧은비밀번호_400반환() {
        assertThat(register("user@test.com", "short"))
                .hasStatus(400)
                .bodyJson().extractingPath("$.detail").asString().isEqualTo("Validation failed");
    }

    // ==================== verify ====================

    @Test
    void 이메일인증_성공_200반환() {
        register("user@test.com", "password123");
        String token = tokenJpaRepository.findAll().get(0).getToken();

        assertThat(verify(token))
                .hasStatus(200)
                .bodyJson().extractingPath("$.message").asString().isNotBlank();

        assertThat(tokenJpaRepository.findAll()).isEmpty();
        assertThat(userJpaRepository.findByEmail("user@test.com").get().isVerified()).isTrue();
    }

    @Test
    void 이메일인증_유효하지않은토큰_400반환() {
        assertThat(verify("invalid-token-value"))
                .hasStatus(400);
    }

    // ==================== login ====================

    @Test
    void 로그인_인증완료후_성공_200반환() {
        register("user@test.com", "password123");
        String token = tokenJpaRepository.findAll().get(0).getToken();
        verify(token);

        assertThat(login("user@test.com", "password123"))
                .hasStatus(200)
                .bodyJson().extractingPath("$.accessToken").asString().isNotBlank();
    }

    @Test
    void 로그인_미인증유저_403반환() {
        register("user@test.com", "password123");

        assertThat(login("user@test.com", "password123"))
                .hasStatus(403);
    }

    @Test
    void 로그인_존재하지않는이메일_401반환() {
        assertThat(login("none@test.com", "password123"))
                .hasStatus(401);
    }

    @Test
    void 로그인_잘못된비밀번호_401반환() {
        register("user@test.com", "password123");
        String token = tokenJpaRepository.findAll().get(0).getToken();
        verify(token);

        assertThat(login("user@test.com", "wrongPassword"))
                .hasStatus(401);
    }

    // ==================== helpers ====================

    private MvcTestResult register(String email, String password) {
        return mvc.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","password":"%s"}
                        """.formatted(email, password))
                .exchange();
    }

    private MvcTestResult verify(String token) {
        return mvc.patch().uri("/api/v1/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"token":"%s"}
                        """.formatted(token))
                .exchange();
    }

    private MvcTestResult login(String email, String password) {
        return mvc.post().uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","password":"%s"}
                        """.formatted(email, password))
                .exchange();
    }
}
