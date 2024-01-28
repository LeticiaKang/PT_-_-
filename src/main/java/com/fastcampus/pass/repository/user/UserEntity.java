package com.fastcampus.pass.repository.user;

import com.fastcampus.pass.repository.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {
    @Id
    private String userId;

    private String userName;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private String phone;

    // String을 json으로 바꿔서 정의함. (저장되어 있는 문자열 데이터를 Map으로 매핑)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> meta;

    public String getUuid() {  //meta에 uuid가 있으면, uuid를 반환. 없으면 null 반환.
        String uuid = null;
        if (meta.containsKey("uuid")) {
            uuid = String.valueOf(meta.get("uuid"));
        }
        return uuid;

    }

}
