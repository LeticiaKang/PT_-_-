package com.fastcampus.pass;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing  // 배치를 돌리기 위함
@SpringBootApplication
public class PassBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(PassBatchApplication.class, args);
	}

	// 배치를 만들기 위해선 스템을 만들고, 스텝을 만들기 위해선, 잡을 만들어야 한다

	// 잡 구현
	@Bean
	public Job passJob(JobRepository jobRepository, Step passStep) {
		return new JobBuilder("passStep", jobRepository)
				.start(passStep)  // start(스텝)
				.build();
	}

	// 스템 구현
	@Bean
	public Step passStep(final JobRepository jobRepository,
						 final Tasklet passTasklet,
						 final PlatformTransactionManager platformTransactionManager) {
		return new StepBuilder("passStep", jobRepository)
				.tasklet(passTasklet, platformTransactionManager)
				.build();
	}

	//Tasklet 구현
	@Bean
	public Tasklet passTasklet() {
		return ((contribution, chunkContext) -> {
			System.out.println("Execute PassStep");
			return RepeatStatus.FINISHED;
		});
	}


}


