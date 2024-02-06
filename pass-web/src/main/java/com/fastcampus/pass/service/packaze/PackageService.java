package com.fastcampus.pass.service.packaze;

import com.fastcampus.pass.repository.packaze.PackageEntity;
import com.fastcampus.pass.repository.packaze.PackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageService {
    // 필드
    private final PackageRepository packageRepository;

    // 생성자
    public PackageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    // 패키지 이름으로 정렬하여 모든 패키지 조회
    public List<Package> getAllPackages() {
        List<PackageEntity> bulkPassEntities = packageRepository.findAllByOrderByPackageName();
        return PackageModelMapper.INSTANCE.map(bulkPassEntities);
    }
}
