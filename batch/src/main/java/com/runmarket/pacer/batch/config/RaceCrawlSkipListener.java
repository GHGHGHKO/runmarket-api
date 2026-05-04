package com.runmarket.pacer.batch.config;

import com.runmarket.pacer.domain.port.in.race.SaveRaceCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RaceCrawlSkipListener {

    @OnSkipInRead
    public void onSkipInRead(Throwable t) {
        log.error("목록 읽기 중 오류: {}", t.getMessage());
    }

    @OnSkipInProcess
    public void onSkipInProcess(Integer no, Throwable t) {
        log.error("no={} 처리 중 오류: {}", no, t.getMessage());
    }

    @OnSkipInWrite
    public void onSkipInWrite(SaveRaceCommand command, Throwable t) {
        log.error("대회명={} (no={}) 저장 중 오류: {}", command.name(), command.externalId(), t.getMessage());
    }
}
