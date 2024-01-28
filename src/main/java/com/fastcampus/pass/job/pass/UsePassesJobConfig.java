package com.fastcampus.pass.job.pass;

import com.fastcampus.pass.repository.booking.BookingEntity;
import com.fastcampus.pass.repository.booking.BookingRepository;
import com.fastcampus.pass.repository.booking.BookingStatus;
import com.fastcampus.pass.repository.pass.PassEntity;
import com.fastcampus.pass.repository.pass.PassRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Future;

@Configuration
@RequiredArgsConstructor
public class UsePassesJobConfig {

    private final int CHUNK_SIZE = 10;
    private final EntityManagerFactory entityManagerFactory; //영속성 관리
    private final PassRepository passRepository;
    private final BookingRepository bookingRepository;

    //job구현
    @Bean
    Job usePassesJob(final JobRepository jobRepository,
                     final Step usePassesStep) {
        return new JobBuilder("usePassesJob", jobRepository)
                .start(usePassesStep)
                .build();
    }

    //step 구현 - 업데이트를 해야하기 때문에 cursor
    @Bean
    Step usePassesStep(final JobRepository jobRepository,
                       final PlatformTransactionManager platformTransactionManager,
                       final ItemReader<BookingEntity> usePassesItemReader,
                       final ItemProcessor<BookingEntity, Future<BookingEntity>> usePassesAsyncItemProcessor,
                       final ItemWriter<Future<BookingEntity>> usePassesAsyncItemWriter) {
        return new StepBuilder("usePassesStep", jobRepository)
                .<BookingEntity, Future<BookingEntity>>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(usePassesItemReader)
                .processor(usePassesAsyncItemProcessor)
                .writer(usePassesAsyncItemWriter)
                .build();
    }

    //reader메서드
    @Bean
    public JpaCursorItemReader<BookingEntity> usePassesItemReader() {
        return new JpaCursorItemReaderBuilder<BookingEntity>()
                .name("usePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                // 상태(status)가 완료이며, 종료 일시(endedAt)이 과거인 예약이 이용권 차감 대상이 됩니다.
                .queryString("select b from BookingEntity b join fetch b.passEntity where b.status = :status and b.usedPass = false and b.endedAt < :endedAt")
                .parameterValues(Map.of("status", BookingStatus.COMPLETED, "endedAt", LocalDateTime.now()))
                .build();
    }

    //processor
    // 이 프로젝트에서는 적합하지 않지만, ItemProcessor의 수행이 오래걸려 병목이 생기는 경우에 AsyncItemProcessor, AsyncItemWriter를 사용하면 성능을 향상시킬 수 있습니다.
    @Bean
    public AsyncItemProcessor<BookingEntity, BookingEntity> usePassesAsyncItemProcessor() {
        AsyncItemProcessor<BookingEntity, BookingEntity> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(usePassesItemProcessor());               // usePassesItemProcessor로 위임, 결과를 Future에 저장
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());      // 새로운 스레드에서 실행
        return asyncItemProcessor;

    }

    @Bean
    public ItemProcessor<BookingEntity, BookingEntity> usePassesItemProcessor() {
        return bookingEntity -> {
            // 이용권 잔여 횟수는 차감합니다.
            PassEntity passEntity = bookingEntity.getPassEntity();
            passEntity.setRemainingCount(passEntity.getRemainingCount() - 1);
            bookingEntity.setPassEntity(passEntity);

            // 이용권 사용 여부를 업데이트합니다.
            bookingEntity.setUsedPass(true);
            return bookingEntity;
        };
    }

    // writer
    // usePassesItemWriter 최종 결과값을 넘겨주고 작업을 위임합니다.
    @Bean
    public AsyncItemWriter<BookingEntity> usePassesAsyncItemWriter() {
        AsyncItemWriter<BookingEntity> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(usePassesItemWriter());
        return asyncItemWriter;
    }

    @Bean
    public ItemWriter<BookingEntity> usePassesItemWriter() {
        return bookingEntities -> {
            for (BookingEntity bookingEntity : bookingEntities) {
                // 잔여 횟수를 업데이트 합니다.
                int updatedCount = passRepository.updateRemainingCount(bookingEntity.getPassSeq(), bookingEntity.getPassEntity().getRemainingCount());
                // 잔여 횟수가 업데이트 완료되면, 이용권 사용 여부를 업데이트합니다.
                if (updatedCount > 0) {
                    bookingRepository.updateUsedPass(bookingEntity.getPassSeq(), bookingEntity.isUsedPass());
                }
            }
        };
    }

}
