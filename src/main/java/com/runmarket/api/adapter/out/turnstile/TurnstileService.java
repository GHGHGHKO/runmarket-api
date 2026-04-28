package com.runmarket.api.adapter.out.turnstile;

import com.runmarket.api.domain.exception.TurnstileVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class TurnstileService {

    private static final String SITEVERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    private final String secretKey;
    private final RestClient restClient;

    public TurnstileService(@Value("${turnstile.secret-key}") String secretKey) {
        this.secretKey = secretKey;
        this.restClient = RestClient.create();
    }

    public void verify(String token) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("secret", secretKey);
        formData.add("response", token);

        TurnstileResponse response = restClient.post()
                .uri(SITEVERIFY_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(TurnstileResponse.class);

        if (response == null || !response.success()) {
            log.warn("Turnstile verification failed: errorCodes={}", response != null ? response.errorCodes() : "null response");
            throw new TurnstileVerificationException();
        }
    }
}
