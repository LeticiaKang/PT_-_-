package com.fastcampus.pass.repository.user;

import com.fastcampus.pass.repository.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
public class UserEntity  {
    @Id
    private String userId;

    private String userName;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private String phone;
    private String meta;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
