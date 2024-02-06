package com.fastcampus.pass.service.statistics;

import com.fastcampus.pass.repository.statistics.StatisticsRepository;
import com.fastcampus.pass.util.LocalDateTimeUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    public StatisticsService(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    // 차트 데이터를 만들어 주는 메서드
    public ChartData makeChartData(final LocalDateTime to) {
        // to 에서 10일을 뺌
        final LocalDateTime from = to.minusDays(10);

        final List<AggregatedStatistics> aggregatedStatisticsList = statisticsRepository.findByStatisticsAtBetweenAndGroupBy(from, to);
        // 라벨, 출석 횟수, 취소 횟수
        List<String> labels = new ArrayList<>();
        List<Long> attendedCounts = new ArrayList<>();
        List<Long> cancelledCounts = new ArrayList<>();

        for (AggregatedStatistics statistics : aggregatedStatisticsList) {
            labels.add(LocalDateTimeUtils.format(statistics.getStatisticsAt(), LocalDateTimeUtils.MM_DD));
            attendedCounts.add(statistics.getAttendedCount());
            cancelledCounts.add(statistics.getCancelledCount());
        }
        return new ChartData(labels, attendedCounts, cancelledCounts);

    }
}
