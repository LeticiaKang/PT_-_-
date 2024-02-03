package com.fastcampus.pass.repository.pass;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
// ReportingPolicy.IGNORE: 일치하지 않은 필드를 무시 /  MapStruct Code Generator가 해당 인터페이스의 구현체를 생성
public interface PassModelMapper {
    PassModelMapper INSTANCE = Mappers.getMapper(PassModelMapper.class);        // 생성된 Mapper 인스턴스를 받아옴(인터페이스 매퍼에 인스턴스를 선언)
                                                                                // @Mapping는 구현체 생성 시 soruce가 되는 클래스와 target이 되는 클래스의 속성명을 비교하고, 자동으로 매핑 코드를 작성한다.
    @Mapping(target = "status", qualifiedByName = "defaultStatus")              // 필드명이 같지 않거나 custom하게 매핑해주기도 한다.
    @Mapping(target = "remainingCount", source = "bulkPassEntity.count")
    PassEntity toPassEntity(BulkPassEntity bulkPassEntity, String userId);      // 인자를 bulkPassEntity, userId로, 리턴 타입을 PassEntity로

    // BulkPassStatus와 관계 없이 PassStatus는 READY를 선언
    @Named("defaultStatus")
    default PassStatus status(BulkPassStatus status) {
        return PassStatus.READY;
    }

}
