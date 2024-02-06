package com.fastcampus.pass.service.user;

import com.fastcampus.pass.repository.user.UserGroupMappingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserGroupMappingService {
    // 필드
    private final UserGroupMappingRepository userGroupMappingRepository;

    // 생성자
    public UserGroupMappingService(UserGroupMappingRepository userGroupMappingRepository) {
        this.userGroupMappingRepository = userGroupMappingRepository;
    }

    // user group id를 중복없이 역순으로 조회
    public List<String> getAllUserGroupIds() {
        return userGroupMappingRepository.findDistinctUserGroupId();

    }
}
