package com.fastcampus.pass.service.pass;

import com.fastcampus.pass.controller.admin.BulkPassRequest;
import com.fastcampus.pass.repository.packaze.PackageEntity;
import com.fastcampus.pass.repository.packaze.PackageRepository;
import com.fastcampus.pass.repository.pass.BulkPassEntity;
import com.fastcampus.pass.repository.pass.BulkPassRepository;
import com.fastcampus.pass.repository.pass.BulkPassStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BulkPassService {
    // 필드
    private final BulkPassRepository bulkPassRepository;
    private final PackageRepository packageRepository;

    // 생성자
    public BulkPassService(BulkPassRepository bulkPassRepository, PackageRepository packageRepository) {
        this.bulkPassRepository = bulkPassRepository;
        this.packageRepository = packageRepository;
    }

    // 시작을 기준으로 내림차순 한 BulkPass 데이터 목록을 반환하는 메서드
    public List<BulkPass> getAllBulkPasses() {
        // 시작일 기준으로 내림차순 한 데이터 목록을 가져옴
        List<BulkPassEntity> bulkPassEntities = bulkPassRepository.findAllOrderByStartedAtDesc();
        // BulkPassEntity목록을 BulkPass로 매핑하여 반환
        return BulkPassModelMapper.INSTANCE.map(bulkPassEntities);
    }

    // BulkPassRequest를 기반으로 pass Entity를 생성하여, DB에 저장하는 메서드
    public void addBulkPass(BulkPassRequest bulkPassRequest) {
        // package 정보를 가져옴(packageSeq를 기준으로 가져옴)
        PackageEntity packageEntity = packageRepository.findById(bulkPassRequest.getPackageSeq()).orElseThrow();

        // 매핑을 이용하여 BulkPassEntity로 변경해준다
        BulkPassEntity bulkPassEntity = BulkPassModelMapper.INSTANCE.map(bulkPassRequest);
        // 상태가 미정이므로, 기본값인 "준비중"으로 변경
        bulkPassEntity.setStatus(BulkPassStatus.READY);
        // package에서 가져온 값으로 지정
        bulkPassEntity.setCount(packageEntity.getCount());
        // package에서 가져온 값으로 끝나는 날을 정함
        // setEndedAt : (period가 null이면 패스, null이 아니면 startedAt에 period를 더해준 값을 반환함)
        bulkPassEntity.setEndedAt(packageEntity.getPeriod());
        // bulkPassRepository에 저장
        bulkPassRepository.save(bulkPassEntity);
    }

}
