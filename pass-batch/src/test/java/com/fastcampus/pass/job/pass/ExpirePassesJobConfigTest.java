package com.fastcampus.pass.job.pass;

import com.fastcampus.pass.config.TestBatchConfig;
import com.fastcampus.pass.repository.pass.PassEntity;
import com.fastcampus.pass.repository.pass.PassRepository;
import com.fastcampus.pass.repository.pass.PassStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {ExpirePassesJobConfig.class, TestBatchConfig.class})
public class ExpirePassesJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private PassRepository passRepository;

    @Test
    public void test_expirePassesStep() throws Exception {
        // given
        addPassEntities(10);
        System.out.println(jobLauncherTestUtils.launchJob());
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();                  //JobExecution: BATCH_JOB_EXECUTION 테이블과 매핑(항상 새로 생성)
        JobInstance jobInstance = jobExecution.getJobInstance();                       //JobInstance: BATCH_JOB_INSTANCE 테이블과 매핑

        // then
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);      //job이 돌아가서 만료된 pass의 결과가 COMPLETED가 맞는지 확인
        assertThat(jobInstance.getJobName()).isEqualTo("expirePassesJob");   //job이름 expirePassesJob가 맍는지 확인
    }

    private void addPassEntities(int size) {
        final LocalDateTime now = LocalDateTime.now();
        final Random random = new Random();

        List<PassEntity> passEntities = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            PassEntity passEntity = new PassEntity();                   //pass를 추가할 인스턴스 생성
            passEntity.setPackageSeq(1);                                //패키지 번호를 모두 1로 추가
            passEntity.setUserId("A" + 1000000 + i);                    //user ID는 뒤에만 바꿔서 추가
            passEntity.setStatus(PassStatus.PROGRESSED);                //상태는 진행(profressed)으로 성정
            passEntity.setRemainingCount(random.nextInt(11));    //잔여 수강 횟수는 0~11안에서 랜덤하게
            passEntity.setStartedAt(now.minusDays(60));                 //시작 날짜는 현재 시점에서 2달 전으로 설정
            passEntity.setEndedAt(now.minusDays(1));                    //끝나는 날자는 현재 시점에서 1일 전으로 설정(어제)
            passEntities.add(passEntity);                               //위 설정을 토대로 pass에 저장

        }
        passRepository.saveAll(passEntities);
    }

}
