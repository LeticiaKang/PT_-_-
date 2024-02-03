package com.fastcampus.pass.job.statistics;

import com.fastcampus.pass.repository.statistics.AggregatedStatistics;
import com.fastcampus.pass.repository.statistics.StatisticsRepository;
import com.fastcampus.pass.util.CustomCSVWriter;
import com.fastcampus.pass.util.LocalDateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@StepScope
public class MakeWeeklyStatisticsTasklet implements Tasklet {

    // jobParameters를 가져와야 시작점을 알 수 있기 때문에, step scope 선언
    @Value("#{jobParameters[from]}")
    private String fromString;
    @Value("#{jobParameters[to]}")
    private String toString;

    private final StatisticsRepository statisticsRepository;

    // 생성자 
    public MakeWeeklyStatisticsTasklet(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }
    
    // 오버라이딩
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        // jobParameters에서 시작 날짜와 종료 날짜를 가져와 LocalDateTime 형식으로 변환
        final LocalDateTime from = LocalDateTimeUtils.parse(fromString);
        final LocalDateTime to = LocalDateTimeUtils.parse(toString);

        // 통계를 가져오는 메서드를 호출하여 시작 날짜부터 종료 날짜까지의 통계를 조회
        final List<AggregatedStatistics> statisticsList = statisticsRepository.findByStatisticsAtBetweenAndGroupBy(from, to);

        // 주간 통계를 저장하기 위한 Map 생성
        Map<Integer, AggregatedStatistics> weeklyStatisticsEntityMap = new LinkedHashMap<>();

        // 조회된 통계 데이터를 주간 단위로 그룹화하여 Map에 저장
        for (AggregatedStatistics statistics : statisticsList) {
            // 통계 데이터의 주차 정보를 가져옴
            int week = LocalDateTimeUtils.getWeekOfYear(statistics.getStatisticsAt());
            // 해당 주차에 대한 통계 데이터를 Map에서 조회
            AggregatedStatistics savedStatisticsEntity = weeklyStatisticsEntityMap.get(week);

            // 해당 주차의 통계 데이터가 없는 경우, Map에 추가
            if (savedStatisticsEntity == null) {
                weeklyStatisticsEntityMap.put(week, statistics);
            } else {
                // 이미 해당 주차에 대한 통계 데이터가 있는 경우, 통계 데이터를 합산
                savedStatisticsEntity.merge(statistics);
            }
        }

        // 주간 통계 데이터를 CSV 파일로 작성하기 위한 리스트 생성 및 헤더 추가
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"week", "allCount", "attendedCount", "cancelledCount"});

        // 주간 통계 데이터를 리스트에 추가
        weeklyStatisticsEntityMap.forEach((week, statistics) -> {
            data.add(new String[]{
                    "Week " + week,
                    String.valueOf(statistics.getAllCount()),
                    String.valueOf(statistics.getAttendedCount()),
                    String.valueOf(statistics.getCancelledCount())
            });
        });

        // 생성된 주간 통계 데이터를 CSV 파일로 작성
        CustomCSVWriter.write("weekly_statistics_" + LocalDateTimeUtils.format(from, LocalDateTimeUtils.YYYY_MM_DD) + ".csv", data);

        // 작업이 완료되었음을 Batch에 알림
        return RepeatStatus.FINISHED;
    }
}
