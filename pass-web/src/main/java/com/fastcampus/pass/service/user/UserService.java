package com.fastcampus.pass.service.user;

import com.fastcampus.pass.repository.user.UserEntity;
import com.fastcampus.pass.repository.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 아이디로 User 정보를 가져오는 함수
    public User getUser(final String userId) {
        
        UserEntity userEntity = userRepository.findByUserId(userId);

        return UserModelMapper.INSTANCE.toUser(userEntity);

    }
}
