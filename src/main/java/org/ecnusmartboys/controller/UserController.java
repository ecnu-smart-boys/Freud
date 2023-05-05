package org.ecnusmartboys.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.annotation.AnonymousAccess;
import org.ecnusmartboys.mapstruct.UserInfoMapper;
import org.ecnusmartboys.model.dto.UserInfo;
import org.ecnusmartboys.model.response.BaseResponse;
import org.ecnusmartboys.service.UserService;
import org.ecnusmartboys.utils.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Api(tags = "用户接口")
public class UserController {

    private final UserService userService;

    private final UserInfoMapper userInfoMapper;

    @AnonymousAccess
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public BaseResponse<UserInfo> getUserInfo(){
        var userId = SecurityUtil.getCurrentUserId();
        var u = userService.getById(userId);
        return BaseResponse.ok(userInfoMapper.toDto(u));
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
