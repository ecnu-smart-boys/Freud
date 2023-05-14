package org.ecnusmartboys.api.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.annotation.AnonymousAccess;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.infrastructure.model.mysql.User;
import org.ecnusmartboys.infrastructure.model.mysql.Visitor;
import org.ecnusmartboys.application.dto.request.command.UpdateUserInfoReq;
import org.ecnusmartboys.application.dto.response.Response;
import org.ecnusmartboys.domain.repository.VisitorRepository;
import org.ecnusmartboys.domain.service.UserService;
import org.ecnusmartboys.infrastructure.utils.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "微信用户接口")
public class UserController {

    private final UserService userService;
    public static final String userServiceName = "userService";

    private final VisitorRepository visitorRepository;

    public UserController(UserService userService, VisitorRepository visitorRepository) {
        this.userService = userService;
        this.visitorRepository = visitorRepository;
    }

    @AnonymousAccess
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public Response<UserInfo> getUserInfo() {
        var userId = SecurityUtil.getCurrentUserId();
        return Response.ok(userService.getUserInfo(userId));
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/info")
    public Response<Object> updateUserInfo(@RequestBody @Validated UpdateUserInfoReq req) {
        // 更新用户信息
        var userId = SecurityUtil.getCurrentUserId();
        var user = new User();
        BeanUtils.copyProperties(req, user);
        user.setId(userId);
        userService.updateById(user);

        // 更新访客信息
        var visitor = new Visitor();
        visitor.setId(userId);
        visitor.setEmergencyContact(req.getEmergencyContact());
        visitor.setEmergencyPhone(req.getEmergencyPhone());
        visitorRepository.updateById(visitor);

        return Response.ok();
    }
}
