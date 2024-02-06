package com.fastcampus.pass.service.pass;

import com.fastcampus.pass.repository.pass.PassEntity;
import com.fastcampus.pass.repository.pass.PassRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassService { // 이용권 관련 비지니스 로직을 처리함
    // 필드 (이용권 레파지토리)
    private final PassRepository passRepository;

    // 생성자
    public PassService(PassRepository passRepository) {
        this.passRepository = passRepository;
    }

    // userId를 이용해 이용권 정보를 가져오는 함수
    public List<Pass> getPasses(final String userId) {
        // 필드를 변경하면 안되기 때문에 final로 정의
        final List<PassEntity> passEntities = passRepository.findByUserId(userId);
        // PassEntity와 Pass 사이 매핑 → 리스트<pass>로 반환됨
        return PassModelMapper.INSTANCE.map(passEntities);

    }

}
