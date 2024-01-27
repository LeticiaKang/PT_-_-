package com.fastcampus.pass.job.pass;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AddPassesJobConfig {
// tasklet은 따로 빼서 작성했음

    // Job구현
    @Bean
    Job addPassesJob(final JobRepository jobRepository,
                     final Step addPassesStep) {
        return new JobBuilder("addPassesJob", jobRepository)
                .start(addPassesStep)
                .build();
    }

    // Step 구현
    @Bean
    Step addPassesStep(final JobRepository jobRepository,
                       final PlatformTransactionManager platformTransactionManager,
                       final Tasklet addPassesTasklet) {
        return new StepBuilder("addPassesStep", jobRepository)
                .tasklet(addPassesTasklet, platformTransactionManager)
                //이용권 만료 job에서는 reader, processor, writer를 따로 작성하여 실행했는데, 여기선 tasklet을 넣어서 완료한다.
                .build();
    }

}
