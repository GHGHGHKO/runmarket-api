package com.runmarket.pacer.batch.crawler;

import com.runmarket.pacer.domain.port.in.race.SaveRaceCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class CrawlStateStore {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Path stateFile;
    private final Map<String, String> state;

    public CrawlStateStore(@Value("${marathon-race.state-file:crawl_state.json}") String path) {
        this.stateFile = Path.of(path);
        this.state = loadState();
        log.info("상태 파일 로드 완료: {}건 ({})", state.size(), stateFile.toAbsolutePath());
    }

    public String computeHash(SaveRaceCommand command) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(command.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean isUnchanged(int no, String hash) {
        return hash.equals(state.get(String.valueOf(no)));
    }

    public void markUpdated(int no, String hash) {
        state.put(String.valueOf(no), hash);
    }

    public void flush() {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(stateFile.toFile(), state);
        } catch (IOException e) {
            log.error("상태 파일 저장 실패: {}", e.getMessage());
        }
    }

    private Map<String, String> loadState() {
        if (!Files.exists(stateFile)) return new LinkedHashMap<>();
        try {
            return MAPPER.readValue(stateFile.toFile(), new TypeReference<LinkedHashMap<String, String>>() {});
        } catch (IOException e) {
            log.warn("상태 파일 로드 실패, 빈 상태로 시작: {}", e.getMessage());
            return new LinkedHashMap<>();
        }
    }
}
