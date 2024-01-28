package com.fastcampus.pass.repository.booking;

import com.fastcampus.pass.repository.BaseEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserEntity userEntity;

}
