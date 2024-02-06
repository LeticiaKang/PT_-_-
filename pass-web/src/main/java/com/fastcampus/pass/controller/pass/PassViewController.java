package com.fastcampus.pass.controller.pass;

import com.fastcampus.pass.service.pass.Pass;
import com.fastcampus.pass.service.pass.PassService;
import com.fastcampus.pass.service.user.User;
import com.fastcampus.pass.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor  // 생성자 자동 생성
@RequestMapping(value = "/passes")
public class PassViewController {  // 이용권 조회 페이지를 반환하기 위한 controller
    private final UserService userService;
    private final PassService passService;

//    @GetMapping (value = "/passes")  // 주소 :  /passes, HTTP GET 요청에 응답 (spring4이후 변경 @RequestMapping 생략가능)
    @GetMapping
    public String getPasses(@RequestParam("userId") String userId, Model model) {
        // @RequestParam 애노테이션을 사용하여 URL 쿼리 매개변수 중 "userId" 값을 가져온다.

        final List<Pass> passes = passService.getPasses(userId);   // 유저 Id를 이용하여 pass정보 가져오기
        final User user = userService.getUser(userId);             // 유저 Id를 이용하여 유저 정보 가져오기

        // View에 데이터를 전달 : model.addAttribute() → pass/index로 보내짐
        model.addAttribute("passes", passes);
        model.addAttribute("user", user);

        return "pass/index";
    }

}
