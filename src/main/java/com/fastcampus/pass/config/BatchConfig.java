package com.fastcampus.pass.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.PlatformTransactionManager;

/*
 * @EnableBatchProcessing
 * Spring Batch 기능을 활성화하고 배치 작업을 설정하기 위한 기본 구성을 제공합니다.
 * JobBuilderFactory, StepBuilderFactory를 빈으로 등록되어 Job, Step 구현 시 사용할 수 있습니다.
 */

@Configuration  // 환경 설정 관련 Class 임을 명시
@EnableBatchProcessing  //초기 메타 데이터 생성시 주석 처리하고 돌려야 메타데이터가 생성됨
@EnableConfigurationProperties(BatchProperties.class)
public class BatchConfig {
//    @Bean
//    public Job passJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
//                       Step passStep) {
//        return new JobBuilder("passJob", jobRepository)
//                .start(passStep)
//                .build();
//    }
//
//    @Bean
//    public Step passStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        return new StepBuilder("passStep", jobRepository)
//                .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
//                    System.out.println("테스트 성공!");
//                    return RepeatStatus.FINISHED;
//                }, transactionManager)
//                .build();
//    }

}




