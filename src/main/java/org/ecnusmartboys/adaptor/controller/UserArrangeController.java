package org.ecnusmartboys.adaptor.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.adaptor.Extractor;
import org.ecnusmartboys.adaptor.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.StaffBaseInfo;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.UserListReq;
import org.ecnusmartboys.application.dto.response.ConsultantsResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.dto.response.SupervisorsResponse;
import org.ecnusmartboys.application.dto.response.VisitorsResponse;
import org.ecnusmartboys.application.service.UserArrangeService;
import org.ecnusmartboys.domain.model.user.Admin;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/user_arrange")
@Api(tags = "用户管理接口")
@RequiredArgsConstructor
public class UserArrangeController {

    private final UserArrangeService userArrangeService;


    @AuthRoles(Admin.ROLE)
    @ApiOperation("获取咨询师列表")
    @GetMapping("/consultants")
    public Responses<ConsultantsResponse> getConsultants(@RequestParam("current") @Min(1L) Long current,
                                                         @RequestParam("size") @Min(1L) Long size,
                                                         @RequestParam("name") @Size(max = 32) String name) {
        return userArrangeService.getConsultants(new UserListReq(current, size, name));
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("获取督导列表")
    @GetMapping("/supervisors")
    public Responses<SupervisorsResponse> getSupervisors(@RequestParam("current") @Min(1L) Long current,
                                                         @RequestParam("size") @Min(1L) Long size,
                                                         @RequestParam("name") @Size(max = 32) String name) {
        return userArrangeService.getSupervisors(new UserListReq(current, size, name));
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("获取访客列表")
    @GetMapping("/visitors")
    public Responses<VisitorsResponse> getVisitors(@RequestParam("current") @Min(1L) Long current,
                                                   @RequestParam("size") @Min(1L) Long size,
                                                   @RequestParam("name") @Size(max = 32) String name) {
        return userArrangeService.getVisitors(new UserListReq(current, size, name));
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("禁用用户")
    @PutMapping("/disable")
    public Responses<Object> disable(@RequestBody @Validated DisableUserRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return userArrangeService.disable(req, common);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("启用用户")
    @PutMapping("/enable")
    public Responses<Object> enable(@RequestBody @Validated EnableUserRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return userArrangeService.enable(req, common);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("添加督导")
    @PostMapping("/addSupervisor")
    public Responses<Object> addSupervisor(@RequestBody @Validated AddSupervisorRequest req) {
        return userArrangeService.saveSupervisor(req);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("更新督导")
    @PutMapping("/updateSupervisor")
    public Responses<Object> updateSupervisor(@RequestBody @Validated UpdateSupervisorRequest req) {
        return userArrangeService.updateSupervisor(req);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("添加咨询师")
    @PostMapping("/addConsultant")
    public Responses<Object> addConsultant(@RequestBody @Validated AddConsultantRequest req) {
        return userArrangeService.saveConsultant(req);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("更新咨询师")
    @PutMapping("/updateConsultant")
    public Responses<Object> updateConsultant(@RequestBody @Validated UpdateConsultantRequest req) {
        return userArrangeService.updateConsultant(req);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("更新排班")
    @PostMapping("/updateArrangement")
    public Responses<Object> updateArrangement(@RequestBody @Validated UpdateArrangementRequest req) {
        return userArrangeService.updateArrangement(req);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("查询可以绑定的督导列表")
    @GetMapping("/availableSupervisors")
    public Responses<List<StaffBaseInfo>> getAvailableSupervisors() {
        return userArrangeService.getAvailableSupervisors();
    }

}
