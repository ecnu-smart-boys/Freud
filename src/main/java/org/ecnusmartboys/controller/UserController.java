package org.ecnusmartboys.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.annotation.AnonymousAccess;
import org.ecnusmartboys.model.dto.UserInfo;
import org.ecnusmartboys.model.entity.User;
import org.ecnusmartboys.model.entity.Visitor;
import org.ecnusmartboys.model.request.UpdateUserInfoReq;
import org.ecnusmartboys.model.response.BaseResponse;
import org.ecnusmartboys.repository.VisitorRepository;
import org.ecnusmartboys.service.UserService;
import org.ecnusmartboys.utils.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Api(tags = "微信用户接口")
public class UserController {

    private final UserService userService;

    private final VisitorRepository visitorRepository;

    @AnonymousAccess
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public BaseResponse<UserInfo> getUserInfo() {
        var userId = SecurityUtil.getCurrentUserId();
        return BaseResponse.ok(userService.getUserInfo(userId));
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/info")
    public BaseResponse<Object> updateUserInfo(@RequestBody @Validated UpdateUserInfoReq req) {
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

        return BaseResponse.ok();
    }
}
