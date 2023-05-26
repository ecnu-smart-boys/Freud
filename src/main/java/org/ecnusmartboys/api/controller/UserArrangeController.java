package org.ecnusmartboys.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.UserListReq;
import org.ecnusmartboys.application.dto.response.ConsultantsResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.dto.response.SupervisorsResponse;
import org.ecnusmartboys.application.dto.response.VisitorsResponse;
import org.ecnusmartboys.application.service.UserArrangeService;
import org.ecnusmartboys.domain.model.user.Admin;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public Responses<ConsultantsResponse> getConsultants(@RequestBody @Validated UserListReq req) {
        return userArrangeService.getConsultants(req);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("获取督导列表")
    @GetMapping("/supervisors")
    public Responses<SupervisorsResponse> getSupervisors(@RequestBody @Validated UserListReq req) {
        return userArrangeService.getSupervisors(req);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("获取访客列表")
    @GetMapping("/visitors")
    public Responses<VisitorsResponse> getVisitors(@RequestBody @Validated UserListReq req) {
        return userArrangeService.getVisitors(req);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("禁用用户")
    @PutMapping("/disable/{id}")
    public Responses<Object> disable(@PathVariable String id) {

        userService.disable(id, ROLE_CONSULTANT);
        return Responses.ok("成功禁用咨询师");
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("启用用户")
    @PutMapping("/enable/{id}")
    public Responses<Object> enable(@PathVariable String id) {
        userService.enable(id, ROLE_VISITOR);
        return Responses.ok("成功启用访客");
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("添加督导")
    @PostMapping("/add/supervisor")
    public Responses<Object> addSupervisor(@RequestBody @Validated AddSupervisorRequest req) {
        if(userService.getByUsername(req.getUsername()) != null) {
            throw new BadRequestException("该用户名已存在");
        }

        if(userService.getByPhone(req.getPhone()) != null) {
            throw new BadRequestException("该手机号已被注册");
        }
        userService.saveSupervisor(req);
        return Responses.ok("成功添加督导");
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("更新督导")
    @PostMapping("/update/supervisor")
    public Responses<Object> updateSupervisor(@RequestBody @Validated UpdateSupervisorRequest req) {
        if(userService.getSingleUser(req.getSupervisorId(), ROLE_SUPERVISOR) == null) {
            throw new BadRequestException("该咨询师不存在");
        }
        userService.updateSupervisor(req);
        return Responses.ok("成功更新督导");
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("添加咨询师")
    @PostMapping("/add/consultant")
    public Responses<Object> addConsultant(@RequestBody @Validated AddConsultantRequest req) {
        if(userService.getByUsername(req.getUsername()) != null) {
            throw new BadRequestException("该用户名已存在");
        }

        if(userService.getByPhone(req.getPhone()) != null) {
            throw new BadRequestException("该手机号已被注册");
        }

        var ids = req.getSupervisorIds();
        ids.forEach(id -> {
            if(userService.getSingleUser(id, ROLE_SUPERVISOR) == null) {
                throw new BadRequestException("所要绑定的督导不存在");
            }
        });

        userService.saveConsultant(req);
        return Responses.ok("成功添加咨询师");
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("更新咨询师")
    @PostMapping("/update/consultant")
    public Responses<Object> updateConsultant(@RequestBody @Validated UpdateConsultantRequest req) {
        if(userService.getSingleUser(req.getConsultantId(), ROLE_CONSULTANT) == null) {
            throw new BadRequestException("该咨询师不存在");
        }

        var ids = req.getSupervisorIds();
        ids.forEach(id -> {
            if(userService.getSingleUser(id, ROLE_SUPERVISOR) == null) {
                throw new BadRequestException("所要绑定的督导不存在");
            }
        });
//        userService.updateConsultant(req); TODO
        return Responses.ok("成功更新督导");
    }
}
