package com.fastcampus.pass.job.statistics;

import com.fastcampus.pass.repository.booking.BookingEntity;
import com.fastcampus.pass.repository.statistics.StatisticsEntity;
import com.fastcampus.pass.repository.statistics.StatisticsRepository;
import com.fastcampus.pass.util.LocalDateTimeUtils;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MakeStatisticsJobConfig {
    // 청크 사이즈 선언
    private final int CHUNK_SIZE = 10;

    private final EntityManagerFactory entityManagerFactory;
    private final StatisticsRepository statisticsRepository;
    private final MakeDailyStatisticsTasklet makeDailyStatisticsTasklet;
    private final MakeWeeklyStatisticsTasklet makeWeeklyStatisticsTasklet;

    @Bean
    Job makeStatisticsJob(final JobRepository jobRepository,
                          final Step addStatisticsStep,
                          final Step makeDailyStatisticsStep,
                          final Step makeWeeklyStatisticsStep) {
        /*
         addStatisticsStep, makeDailyStatisticsStep, makeWeeklyStatisticsStep 세 개의 Step이 있는데, 이들을 순차적으로 실행하여 통계를 생성하는 작업을 수행한다.
          이때 Flow를 사용하여 이들을 묶어서 하나의 흐름으로 정의함으로써,
          Job 내에서 실행할 때의 관리가 용이해지고, Flow를 사용하면 재사용성이 높아지고, 코드의 가독성과 유지보수성이 향상된다.
         */

        Flow addStatisticsFlow = new FlowBuilder<Flow>("addStatisticsFlow")
                .start(addStatisticsStep)
                .build();

        Flow makeDailyStatisticsFlow = new FlowBuilder<Flow>("makeDailyStatisticsFlow")
                .start(makeDailyStatisticsStep)
                .build();

        Flow makeWeeklyStatisticsFlow = new FlowBuilder<Flow>("makeWeeklyStatisticsFlow")
                .start(makeWeeklyStatisticsStep)
                .build();

        //병렬로 실행될 통계 생성을 위한 Flow를 정의
        Flow parallelMakeStatisticsFlow = new FlowBuilder<Flow>("parallelMakeStatisticsFlow")
                .split(new SimpleAsyncTaskExecutor())                               // 병렬 실행을 지정(실행을 위해 SimpleAsyncTaskExecutor를 사용함)
                .add(makeDailyStatisticsFlow, makeWeeklyStatisticsFlow)             // 병렬 실행에 makeDailyStatisticsFlow, makeWeeklyStatisticsFlow 추가
                .build();

        return new JobBuilder("makeStatisticsJob", jobRepository)
                .start(addStatisticsFlow)
                .next(parallelMakeStatisticsFlow)               // 일간, 주간 flow가 동시에 돌아감
                .build()
                .build();
    }

    @Bean //병렬처리를 하지 않는 step 구현 - 예약정보를 읽어와서 통계데이터 생성
    public Step addStatisticsStep(final JobRepository jobRepository,
                                  final PlatformTransactionManager platformTransactionManager,
                                  final ItemReader<BookingEntity> addStatisticsItemReader,
                                  final ItemWriter<BookingEntity> addStatisticsItemWriter) {
        return new StepBuilder("addStatisticsStep", jobRepository)
                .<BookingEntity, BookingEntity>chunk(CHUNK_SIZE, platformTransactionManager)  // BookingEntity를 output으로 한 이유는 jpa를 사용하지 않고, custom하게 사용하기 위해
                .reader(addStatisticsItemReader)                   // job parameter를 사용한 reader
                .writer(addStatisticsItemWriter)                   // job parameter를 사용한 writer
                .build();
    }

    @Bean
    @StepScope  // job parameter를 사용하기 위한 어노테이션
    public JpaCursorItemReader<BookingEntity> addStatisticsItemReader(@Value("#{jobParameters[from]}") String fromString,
                                                                      @Value("#{jobParameters[to]}") String toString) {
        final LocalDateTime from = LocalDateTimeUtils.parse(fromString); // string으로 받은 파라미터를 localDateTime으로 변경
        final LocalDateTime to = LocalDateTimeUtils.parse(toString);     // from이 기준 시간 시작, to가 기준 시간 끝

        return new JpaCursorItemReaderBuilder<BookingEntity>()           // BookingEntity 값을 읽어오기
                .name("usePassesItemReader")                             // 아이템 리더 이름 설정
                .entityManagerFactory(entityManagerFactory)
                // JobParameter를 받아 종료 일시(endedAt) 기준으로 통계 대상 예약(Booking)을 조회합니다.
                .queryString("select b from BookingEntity b where b.endedAt between :from and :to")
                .parameterValues(Map.of("from", from, "to", to))    // 파라미터 넣어주기
                .build();
    }

    @Bean
    public ItemWriter<BookingEntity> addStatisticsItemWriter() {
        return bookingEntities -> {
            // 시간 순으로 묶기 위해 LocalDateTime과 StatisticsEntity를 묶었고, key를 순서대로 나열하기 위해 LinkedHashMap을 사용함( 키의 삽입 순서를 유지하기 위해 사용되며, 시간 순서대로 그룹화하는 데 중요 )
            Map<LocalDateTime, StatisticsEntity> statisticsEntityMap = new LinkedHashMap<>();

            for (BookingEntity bookingEntity : bookingEntities) {                           // 읽어온 bookingEntity를 돌면서
                final LocalDateTime statisticsAt = bookingEntity.getStatisticsAt();         // key(statisticsAt)로 할 localDateTime을 <예약>테이블에서 가져옴.(시분초 0으로 set)
                StatisticsEntity statisticsEntity = statisticsEntityMap.get(statisticsAt);  // statisticsEntityMap에서 statisticsAt에 해당하는 value(statisticsEntity형태)를 꺼내오기
                System.out.println(statisticsAt);
                System.out.println(statisticsEntity);

                if (statisticsEntity == null) { // statisticsAt에 해당하는 게 없다면, statisticsEntityMap에 없다는 것을 의미함 -> 추가하기
                    statisticsEntityMap.put(statisticsAt, StatisticsEntity.create(bookingEntity)); // statisticsAt을 키로, StatisticsEntity를 만든다

                } else {
                    statisticsEntity.add(bookingEntity);

                }

            }
            final List<StatisticsEntity> statisticsEntities = new ArrayList<>(statisticsEntityMap.values()); // Collection<StatisticsEntity>형싱에서 저장을 위해 list로 변환
            statisticsRepository.saveAll(statisticsEntities);  // statisticsRepository에 저장하기
            log.info("### addStatisticsStep 종료");            // 종료시 로그 출력

        };
    }

    // 병렬로 처리될 두 개의 스텝들
    @Bean
    public Step makeDailyStatisticsStep(final JobRepository jobRepository,
                                        final PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("makeDailyStatisticsStep", jobRepository)
                .tasklet(makeDailyStatisticsTasklet, platformTransactionManager)  // 일간으로 csv 생성
                .build();
    }

    @Bean
    public Step makeWeeklyStatisticsStep(final JobRepository jobRepository,
                                         final PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("makeWeeklyStatisticsStep", jobRepository)
                .tasklet(makeWeeklyStatisticsTasklet, platformTransactionManager)  // 주간으로 csv 생성
                .build();
    }

}
