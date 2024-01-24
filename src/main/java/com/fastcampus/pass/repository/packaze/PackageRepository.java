package com.fastcampus.pass.repository.packaze;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PackageRepository extends JpaRepository<PackageEntity, Integer> {
    /**
     * PackageRepository 인터페이스를 리포지터리로 만들기 위해 JpaRepository 인터페이스를 상속한다.
     * JpaRepository는 JPA가 제공하는 인터페이스 중 하나로 CRUD 작업을 처리하는 메서드들을 이미 내장하고 있어
     * 데이터 관리 작업을 좀 더 편리하게 처리할 수 있다.
     * JpaRepository<PackageRepository, Integer>는 PackageEntity 엔티티로 리포지터리를 생성한다는 의미이다.
     * PackageEntity 엔티티의 기본키가 Integer임을 이와 같이 추가로 지정해야 한다.
     */

}
