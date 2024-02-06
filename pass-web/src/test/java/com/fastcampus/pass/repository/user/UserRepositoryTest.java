package com.fastcampus.pass.repository.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;


@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@EnableJpaAuditing
class UserRepositoryTest {  // JpaRepository<UserEntity, String>

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @DisplayName("saveUser 테스트")
    @Test
    public void saveUser(){
        //given
        UserEntity user = new UserEntity();
        user.setUserId("test2024");
        user.setUserName("홍길동");
        user.setStatus(UserStatus.ACTIVE);
        user.setPhone("010-1234-4567");

        //when
        UserEntity savedUser = userRepository.save(user);

        //Then
        assertNotNull(savedUser.getUserId());
        assertEquals(user, savedUser);
    }
    @DisplayName("FindByUserId 테스트")
    @Test
    public void testFindByUserId() {
        // given

        // when
        UserEntity foundUser = userRepository.findByUserId("test2024");

        // then
        assertEquals("홍길동", foundUser.getUserName());
        assertEquals(UserStatus.ACTIVE, foundUser.getStatus());
        assertEquals("010-1234-4567", foundUser.getPhone());
    }
}
