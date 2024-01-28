package com.fastcampus.pass.repository.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import jakarta.transaction.Transactional;

public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {
    /**
     * BookingRepository 인터페이스를 리포지터리로 만들기 위해 JpaRepository 인터페이스를 상속한다.
     * JpaRepository는 JPA가 제공하는 인터페이스 중 하나로 CRUD 작업을 처리하는 메서드들을 이미 내장하고 있어
     * 데이터 관리 작업을 좀 더 편리하게 처리할 수 있다.
     * JpaRepository<BookingEntity, Integer>는 BookingEntity 엔티티로 리포지터리를 생성한다는 의미이다.
     * BookingEntity 엔티티의 기본키가 Integer임을 이와 같이 추가로 지정해야 한다.
     */

    @Transactional
    @Modifying
    @Query(value = "UPDATE BookingEntity b" +
            "          SET b.usedPass = :usedPass," +
            "              b.modifiedAt = CURRENT_TIMESTAMP" +
            "        WHERE b.passSeq = :passSeq")
    // usedPass라는 컬럼에서 이용권을 사용하지 않은 데이터를 뽑고, 이용권 완료라고 업데이트를 해야하기 때문에 해당 쿼리를 사용
    int updateUsedPass(Integer passSeq, boolean usedPass);

}
