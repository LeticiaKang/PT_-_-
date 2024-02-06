package com.fastcampus.pass.repository.pass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PassRepository extends JpaRepository<PassEntity, Long> {

    @Query(value = """
                    select p from PassEntity p
                    join fetch p.packageEntity
                    where p.userId = :userId
                    order by p.endedAt desc nulls first 
                    """)
    // 이용권, 패키지 테이블을 조인하여 UserId에 해당하는 데이터를 가져오고
    // null(기간 무제한)인 값은 처음으로 보내 사용자가 먼저 볼 수 있게 설정
    List<PassEntity> findByUserId(String userId);
}


