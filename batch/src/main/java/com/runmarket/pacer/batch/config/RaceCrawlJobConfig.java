package com.runmarket.pacer.batch.config;

import com.runmarket.pacer.batch.crawler.MarathonRaceCrawler;
import com.runmarket.pacer.domain.port.in.race.SaveRaceCommand;
import com.runmarket.pacer.domain.port.in.race.SaveRaceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RaceCrawlJobConfig {

    private static final int CHUNK_SIZE = 10;

    private final MarathonRaceCrawler crawler;
    private final SaveRaceUseCase saveRaceUseCase;

    @Bean
    public Job raceCrawlJob(JobRepository jobRepository, Step raceCrawlStep) {
        return new JobBuilder("raceCrawlJob", jobRepository)
                .start(raceCrawlStep)
                .build();
    }

    @Bean
    public Step raceCrawlStep(JobRepository jobRepository) {
        return new StepBuilder("raceCrawlStep", jobRepository)
                .<Integer, SaveRaceCommand>chunk(CHUNK_SIZE)
                .reader(raceNoItemReader())
                .processor(raceDetailItemProcessor())
                .writer(raceItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Integer> raceNoItemReader() {
        return new ListItemReader<>(crawler.fetchRaceNos());
    }

    @Bean
    public ItemProcessor<Integer, SaveRaceCommand> raceDetailItemProcessor() {
        return no -> {
            var command = crawler.fetchRaceDetail(no);
            if (command.isEmpty()) log.warn("no={} 상세 정보 파싱 실패, 건너뜁니다", no);
            return command.orElse(null);
        };
    }

    @Bean
    public ItemWriter<SaveRaceCommand> raceItemWriter() {
        return chunk -> {
            for (SaveRaceCommand command : chunk.getItems()) {
                saveRaceUseCase.save(command);
            }
            log.info("{}건 저장 완료", chunk.getItems().size());
        };
    }
}
