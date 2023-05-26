package org.ecnusmartboys.api.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.Extractor;
import org.ecnusmartboys.api.annotation.AnonymousAccess;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.command.UpdateVisitorRequest;
import org.ecnusmartboys.application.service.UserService;
import org.ecnusmartboys.application.dto.response.Responses;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
}
