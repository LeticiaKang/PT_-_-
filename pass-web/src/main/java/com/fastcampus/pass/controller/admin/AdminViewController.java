package com.fastcampus.pass.controller.admin;

import com.fastcampus.pass.service.packaze.PackageService;
import com.fastcampus.pass.service.pass.BulkPassService;
import com.fastcampus.pass.service.statistics.StatisticsService;
import com.fastcampus.pass.service.user.UserGroupMappingService;
import com.fastcampus.pass.util.LocalDateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping(value = "/admin")
@RequiredArgsConstructor
public class AdminViewController {
    private final BulkPassService bulkPassService;
    private final PackageService packageService;
    private final UserGroupMappingService userGroupMappingService;
    private final StatisticsService statisticsService;

    @GetMapping //차트 뷰 포함
    public String home(@RequestParam(value = "to", required = false) String toString, Model model) {
        LocalDateTime to = Strings.isEmpty(toString) ? LocalDateTime.now() : LocalDateTimeUtils.parseDate(toString);

        // chartData 조회
        model.addAttribute("chartData", statisticsService.makeChartData(to));
        return "admin/index";
    }

    @GetMapping("/bulk-pass")
    public String registerBulkPass(Model model) {
        // bulkPasses 목록 조회
        model.addAttribute("bulkPasses", bulkPassService.getAllBulkPasses());
        // bulkPasses 등록시 필요한 package 조회
        model.addAttribute("packages", packageService.getAllPackages());
        // bulkPasses 등록시 필요한 UserGroupId 조회
        model.addAttribute("userGroupIds", userGroupMappingService.getAllUserGroupIds());
        // bulkPasses request 제공
        model.addAttribute("request", new BulkPassRequest());

        return "admin/bulk-pass";
    }

    @PostMapping("/bulk-pass")
    // BulkPass 생성
    public String addBulkPass(@ModelAttribute("request") BulkPassRequest request, Model model) {
        // 넘겨진 request 값을 기준으로 BulkPass를 추가함
        bulkPassService.addBulkPass(request);
        // 추가된 후 다시 /admin/bulk-pass으로 보냄
        return "redirect:/admin/bulk-pass";
    }
}
