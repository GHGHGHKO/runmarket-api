package com.runmarket.api.adapter.in.web;

import com.runmarket.api.adapter.out.persistence.entity.RoleJpaEntity;
import com.runmarket.api.adapter.out.persistence.repository.EmailVerificationTokenJpaRepository;
import com.runmarket.api.adapter.out.persistence.repository.RaceJpaRepository;
import com.runmarket.api.adapter.out.persistence.repository.RoleJpaRepository;
import com.runmarket.api.adapter.out.persistence.repository.UserJpaRepository;
import com.runmarket.api.domain.model.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class RaceControllerIntegrationTest {

    MockMvcTester mvc;

    @Autowired UserJpaRepository userJpaRepository;
    @Autowired RoleJpaRepository roleJpaRepository;
    @Autowired EmailVerificationTokenJpaRepository tokenJpaRepository;
    @Autowired RaceJpaRepository raceJpaRepository;

    @BeforeEach
    void setUp(@Autowired WebApplicationContext context) {
        mvc = MockMvcTester.from(context, builder ->
                builder.apply(SecurityMockMvcConfigurers.springSecurity()).build());

        raceJpaRepository.deleteAll();
        tokenJpaRepository.deleteAll();
        roleJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    // ==================== PUT /api/v1/races/{id} ====================

    @Test
    void 대회등록_관리자_성공_200반환() {
        String adminToken = createAdminAndGetToken();

        assertThat(saveRace(41278, adminToken))
                .hasStatus(200)
                .bodyJson().extractingPath("$.id").asNumber().isEqualTo(41278);
    }

    @Test
    void 대회등록_동일id_재호출시_upsert_200반환() {
        String adminToken = createAdminAndGetToken();
        saveRace(41278, adminToken);

        assertThat(saveRace(41278, adminToken))
                .hasStatus(200)
                .bodyJson().extractingPath("$.id").asNumber().isEqualTo(41278);

        assertThat(raceJpaRepository.findAll()).hasSize(1);
    }

    @Test
    void 대회등록_일반유저_403반환() {
        assertThat(saveRace(41278, createUserAndGetToken()))
                .hasStatus(403);
    }

    @Test
    void 대회등록_미인증_401반환() {
        assertThat(saveRace(41278, null))
                .hasStatus(401);
    }

    @Test
    void 대회등록_필수값누락_400반환() {
        String adminToken = createAdminAndGetToken();

        assertThat(mvc.put().uri("/api/v1/races/41278")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "테스트 대회"}
                        """)
                .exchange())
                .hasStatus(400);
    }

    // ==================== GET /api/v1/races ====================

    @Test
    void 대회목록조회_성공_200반환() {
        String adminToken = createAdminAndGetToken();
        saveRace(41278, adminToken);
        saveRace(41279, adminToken);

        assertThat(getRaces(null))
                .hasStatus(200)
                .bodyJson().extractingPath("$").asArray().hasSize(2);
    }

    @Test
    void 대회목록조회_미인증_200반환() {
        assertThat(getRaces(null))
                .hasStatus(200);
    }

    // ==================== GET /api/v1/races/{id} ====================

    @Test
    void 대회상세조회_성공_200반환() {
        String adminToken = createAdminAndGetToken();
        saveRace(41278, adminToken);

        assertThat(getRace(41278, null))
                .hasStatus(200)
                .bodyJson().extractingPath("$.id").asNumber().isEqualTo(41278);
    }

    @Test
    void 대회상세조회_존재하지않는대회_404반환() {
        assertThat(getRace(99999, null))
                .hasStatus(404);
    }

    @Test
    void 대회상세조회_미인증_200반환() {
        String adminToken = createAdminAndGetToken();
        saveRace(41278, adminToken);

        assertThat(getRace(41278, null))
                .hasStatus(200);
    }

    // ==================== helpers ====================

    private String createAdminAndGetToken() {
        registerAndVerify("admin@test.com", "password123");
        var user = userJpaRepository.findByEmail("admin@test.com").orElseThrow();
        roleJpaRepository.save(RoleJpaEntity.builder()
                .userId(user.getId())
                .roleType(RoleType.ROLE_ADMIN)
                .build());
        return extractToken(login("admin@test.com", "password123"));
    }

    private String createUserAndGetToken() {
        registerAndVerify("user@test.com", "password123");
        return extractToken(login("user@test.com", "password123"));
    }

    private void registerAndVerify(String email, String password) {
        mvc.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","password":"%s"}
                        """.formatted(email, password))
                .exchange();

        var user = userJpaRepository.findByEmail(email).orElseThrow();
        String token = tokenJpaRepository.findAll().stream()
                .filter(t -> t.getUserId().equals(user.getId()))
                .findFirst().orElseThrow().getToken();

        mvc.patch().uri("/api/v1/auth/verify")
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

    private String extractToken(MvcTestResult result) {
        try {
            String body = result.getResponse().getContentAsString();
            return body.replaceAll(".*\"accessToken\":\"([^\"]+)\".*", "$1");
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract token", e);
        }
    }

    private MvcTestResult saveRace(int externalId, String token) {
        var request = mvc.put().uri("/api/v1/races/" + externalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "2026 장수트레일레이스 Spring",
                          "courses": ["70K", "38K-P", "20K"],
                          "date": "2026-04-03",
                          "startTime": "07:00",
                          "venue": "장수종합경기장",
                          "venueAddress": "전북특별자치도 장수군 장수읍 한누리로 378",
                          "region": "전북",
                          "organizer": "락앤런",
                          "phone": "063-353-2025",
                          "registrationStartDate": "2026-01-02",
                          "registrationEndDate": "2026-02-14",
                          "homepageUrl": "http://rocknrun.kr"
                        }
                        """);
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        return request.exchange();
    }

    private MvcTestResult getRaces(String token) {
        var request = mvc.get().uri("/api/v1/races");
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        return request.exchange();
    }

    private MvcTestResult getRace(int externalId, String token) {
        var request = mvc.get().uri("/api/v1/races/" + externalId);
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        return request.exchange();
    }
}
