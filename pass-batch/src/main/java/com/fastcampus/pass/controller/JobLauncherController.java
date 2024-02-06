package com.fastcampus.pass.controller;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("job")
public class JobLauncherController {
// 배치 작업을 실행하는 RESTful API 구현

    private final JobLauncher jobLauncher;  //스프링 배치에서 작업을 실행하는 데 사용되는 인터페이스

    private final JobRegistry jobRegistry;  //스프링 배치 작업을 등록하고 검색하기 위한 인터페이스

    public JobLauncherController(JobLauncher jobLauncher, JobRegistry jobRegistry) {
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }

    @PostMapping("/launcher")
    //  POST 요청을 처리하고, 요청된 작업을 실행하는 메서드 - 요청의 본문으로부터 작업 이름과 작업 매개변수를 읽어와서 해당 작업을 검색한 후 실행합니다. 작업 실행 결과인 ExitStatus를 반환
    public ExitStatus launchJob(@RequestBody JobLauncherRequest request) throws Exception {
        Job job = jobRegistry.getJob(request.getName());   // getJob : 잡 검색
        return this.jobLauncher.run(job, request.getJobParameters()).getExitStatus();

    }
}
