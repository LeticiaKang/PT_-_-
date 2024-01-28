package com.fastcampus.pass.repository.pass;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PassRepository extends JpaRepository<PassEntity, Integer> {
    /**
     * PassRepository 인터페이스를 리포지터리로 만들기 위해 JpaRepository 인터페이스를 상속한다.
     * JpaRepository는 JPA가 제공하는 인터페이스 중 하나로 CRUD 작업을 처리하는 메서드들을 이미 내장하고 있어
     * 데이터 관리 작업을 좀 더 편리하게 처리할 수 있다.
     * JpaRepository<PassEntity, Integer>는 PassEntity 엔티티로 리포지터리를 생성한다는 의미이다.
     * PassEntity 엔티티의 기본키가 Integer임을 이와 같이 추가로 지정해야 한다.
     */

    // 업데이트한 Remaining Count를 DB에 저장하기 위한 함수
    @Transactional  //해당 메서드가 하나의 트랜잭션으로 실행되야 한다는 것을 의미(성공 또는 실패)
    @Modifying      //이게 없으면 JPA에서는 SELECT문으로 예상하고 실행한다. DB를 수정하는 쿼리라는 것을 의미한다.
    @Query(value = """
            UPDATE PassEntity p
                SET p.remainingCount = :remainingCount
                , p.modifiedAt = CURRENT_TIMESTAMP
                WHERE p.passSeq = :passSeq
            """)
    int updateRemainingCount(Integer passSeq, Integer remainingCount);

}
