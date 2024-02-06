package com.fastcampus.pass.repository.packaze;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageRepository extends JpaRepository<PackageEntity, Integer> {
    
    // 패키지 이름으로 정렬
    List<PackageEntity> findAllByOrderByPackageName();

}
