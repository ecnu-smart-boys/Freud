package org.ecnusmartboys.adaptor.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.Super;
import org.ecnusmartboys.adaptor.Extractor;
import org.ecnusmartboys.adaptor.annotation.AnonymousAccess;
import org.ecnusmartboys.adaptor.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.command.UpdatePsdAndAvatarRequest;
import org.ecnusmartboys.application.dto.request.command.UpdateVisitorRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.UserService;
import org.ecnusmartboys.domain.model.user.Admin;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "微信用户接口")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @AnonymousAccess
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public Responses<UserInfo> getUserInfo(HttpServletRequest request) {
        var common = Extractor.extract(request);
        return userService.getUserInfo(common);
    }

    @ApiOperation("更新访客用户信息")
    @PutMapping("/info")
    public Responses<Object> updateVisitorInfo(@RequestBody @Validated UpdateVisitorRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return userService.updateVisitorInfo(req, common);
    }

    @ApiOperation("保存头像")
    @AuthRoles({Visitor.ROLE, Consultant.ROLE, Supervisor.ROLE, Admin.ROLE})
    @PostMapping("/saveAvatar")
    public Responses<String> saveAvatar(MultipartFile file, HttpServletRequest request)  {
        var common  = Extractor.extract(request);
        return userService.saveAvatar(file, common);
    }

    @ApiOperation("修改密码和头像")
    @AuthRoles({Consultant.ROLE, Supervisor.ROLE})
    @PostMapping("updatePsdAndAvatar")
    public Responses<String> updatePsdAndAvatar(@RequestBody @Validated UpdatePsdAndAvatarRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return userService.updatePsdAndAvatar(req, common);
    }
}
