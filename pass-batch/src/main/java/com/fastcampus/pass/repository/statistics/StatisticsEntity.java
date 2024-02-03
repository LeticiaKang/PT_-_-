package com.fastcampus.pass.repository.statistics;

import com.fastcampus.pass.repository.booking.BookingEntity;
import com.fastcampus.pass.repository.booking.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "statistics")
public class StatisticsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statisticsSeq;
    private LocalDateTime statisticsAt; // 일 단위

    private int allCount;
    private int attendedCount;
    private int cancelledCount;

    public static StatisticsEntity create(final BookingEntity bookingEntity) {
        StatisticsEntity statisticsEntity = new StatisticsEntity();         // StatisticsEntity 객체 생성
        statisticsEntity.setStatisticsAt(bookingEntity.getStatisticsAt());  // bookingEntity에서 endedAt의 시간정보(일별, 시분초 set 0)를 가져와서 StatisticsAt 필드에 추가
        statisticsEntity.setAllCount(1);                                    // allCount 필드를 1로 설정
        if (bookingEntity.isAttended()) {                                   // <예약>테이블에서 참석여부 필드가 True면, attendedCount를 1로 저장
            statisticsEntity.setAttendedCount(1);
        }
        if (BookingStatus.CANCELLED.equals(bookingEntity.getStatus())) {    //  <예약>테이블에서 상태가 취소되어있다면, cancelledCount를 1로 저장
            statisticsEntity.setCancelledCount(1);
        }
        return statisticsEntity;
    }

    public void add(final BookingEntity bookingEntity) {
        this.allCount++;

        if (bookingEntity.isAttended()) {
            this.attendedCount++;

        }
        if (BookingStatus.CANCELLED.equals(bookingEntity.getStatus())) {
            this.cancelledCount++;

        }

    }

}
