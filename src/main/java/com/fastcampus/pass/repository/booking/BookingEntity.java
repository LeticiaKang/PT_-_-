package com.fastcampus.pass.repository.booking;

import com.fastcampus.pass.repository.BaseEntity;
import com.fastcampus.pass.repository.pass.PassEntity;
import com.fastcampus.pass.repository.user.UserEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter  //getter메서드 대체
@Setter  //setter메서드 대체
@ToString //toString메서드 대체
@Entity   //해당 클래스를 entity로 인식시키 위해
@Table(name = "booking") //데이블과 맵핑
public class BookingEntity extends BaseEntity {
    @Id //해당 필드를 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //AUTO_INCREMENT
    private Integer bookingSeq;
    private Integer passSeq;
    private String userId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    private boolean usedPass;
    private boolean attended;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime cancelledAt;
    //생성일시와 수정일시는 BaseEntity를 상속받았으므로 추가하지 않아도 된다

    //user데이터가 필요하기 때문에 userId를 조인 컬럼으로 넣음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserEntity userEntity;

    //pass정보를 가져오기 위해 passSeq를 조인 컬럼으로 넣음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passSeq", insertable = false, updatable = false)
    private PassEntity passEntity;

    // endedAt 기준, yyyy-MM-HH 00:00:00
    public LocalDateTime getStatisticsAt() {
        // 통계를 날짜별로 그룹화하기 위해선, 시간, 분, 초, 밀리초 등의 시간 구성 요소를 모두 0으로 설정하여 해당 날짜의 자정을 기준으로 통계를 집계한다.(일별 집계)
        // endedAt 컬럼에서 가져올때 시,분,초, 나노초 설정을 바꿈
        return this.endedAt.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}
