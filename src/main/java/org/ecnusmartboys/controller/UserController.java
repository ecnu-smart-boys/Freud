package org.ecnusmartboys.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.annotation.AnonymousAccess;
import org.ecnusmartboys.model.dto.UserInfo;
import org.ecnusmartboys.model.entity.User;
import org.ecnusmartboys.model.request.UpdateUserInfoReq;
import org.ecnusmartboys.model.response.BaseResponse;
import org.ecnusmartboys.service.UserService;
import org.ecnusmartboys.utils.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Api(tags = "用户接口")
public class UserController {

    private final UserService userService;

    @AnonymousAccess
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public BaseResponse<UserInfo> getUserInfo(){
        var userId = SecurityUtil.getCurrentUserId();
        return BaseResponse.ok(userService.getUserInfo(userId));
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/info")
    public BaseResponse<?> updateUserInfo(@RequestBody @Validated UpdateUserInfoReq req){
        var userId = SecurityUtil.getCurrentUserId();
        var user = new User();
        BeanUtils.copyProperties(req, user);
        user.setId(userId);
        userService.updateById(user);
        return BaseResponse.ok();
    }

    @AnonymousAccess
    @ApiOperation("登出")
    @PostMapping("/logout")
    public BaseResponse<?> logout(HttpServletRequest request){
        var session = request.getSession();
        session.invalidate();
        return BaseResponse.ok();
    }
}
