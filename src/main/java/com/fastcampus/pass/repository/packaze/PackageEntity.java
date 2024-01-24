package com.fastcampus.pass.repository.packaze;
// package는 reserved word로 사용할 수 없어서 packaze를 사용

import com.fastcampus.pass.repository.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@EqualsAndHashCode
@Table(name = "package")
public class PackageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임 (AUTO_INCREMENT)
    private Integer packageSeq;

    private String packageName;
    private Integer count;
    private Integer period;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
