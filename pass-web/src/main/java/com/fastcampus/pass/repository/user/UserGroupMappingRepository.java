package com.fastcampus.pass.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserGroupMappingRepository extends JpaRepository<UserGroupMappingEntity, Long> {

    // 기존 쿼리는 distinct와 order by가 충돌이 발생해, 서브쿼리로 변경함
//    @Query("select distinct u.userId from UserGroupMappingEntity u order by u.userGroupId")
    @Query("""
            SELECT DISTINCT u.userId FROM UserGroupMappingEntity u WHERE u.userGroupId IN (
                SELECT DISTINCT ugm.userGroupId FROM UserGroupMappingEntity ugm)
            """)
    List<String> findDistinctUserGroupId();
}
