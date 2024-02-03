package com.fastcampus.pass.job.pass;

import com.fastcampus.pass.config.BatchConfig;
import com.fastcampus.pass.repository.pass.PassEntity;
import com.fastcampus.pass.repository.pass.PassStatus;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ExpirePassesJobConfig extends BatchConfig {

    private final int CHUNK_SIZE = 5; //실무는 이것보다 더 크다

    private final EntityManagerFactory entityManagerFactory; //영속성 관리

    @Bean
    Job expirePassesJob(final JobRepository jobRepository,
                        final Step expirePassesStep) {
        return new JobBuilder("expirePassesJob", jobRepository)    //expirePassesJob이라는 이름의 job을 생성
                .start(expirePassesStep)                                 //다음 작업으로 수행할 step 넣기
                .build();
    }

    @Bean
    Step expirePassesStep(final JobRepository jobRepository,
                          final PlatformTransactionManager platformTransactionManager,
                          final ItemReader<PassEntity> expirePassesItemReader,
                          final ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor,
                          final ItemWriter<PassEntity> expirePassesItemWriter) {
        return new StepBuilder("expirePassesStep", jobRepository)
                .<PassEntity, PassEntity>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(expirePassesItemReader)               //데이터에 쿼리를 날려서 조건에 맞는 값을 가져온다
                .processor(expirePassesItemProcessor)         //가져온 값에 대해 작업을 수행함(상태를 progressed -> expired로 변경)
                .writer(expirePassesItemWriter)               //저장
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<PassEntity> expirePassesItemReader() {  //참고 : https://blog.naver.com/rinjyu/222847088860
        return new JpaCursorItemReaderBuilder<PassEntity>()
                .name("expirePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select p from PassEntity p where p.status = :status and p.endedAt <= :endedAt")      // 상태(status)가 진행중이며, 종료일시(endedAt)이 현재 시점보다 과거일 경우 만료 대상이 됩니다.
                .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))  // 위 쿼리의 파라미터 값을 설정
                .build();
    }

    @Bean //Pass엔티티 업데이트하는 processor
    public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
        return passEntity -> {
            passEntity.setStatus(PassStatus.EXPIRED);
            passEntity.setExpiredAt(LocalDateTime.now());
            return passEntity;
        };
    }

    /**
     * JpaItemWriter: JPA의 영속성 관리를 위해 EntityManager를 필수로 설정해줘야 합니다.
     */
    @Bean //DB에 반영하는 writer
    public JpaItemWriter<PassEntity> expirePassesItemWriter() {
        return new JpaItemWriterBuilder<PassEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}
