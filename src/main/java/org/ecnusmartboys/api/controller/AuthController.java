package org.ecnusmartboys.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.annotation.AnonymousAccess;
import org.ecnusmartboys.api.constance.SessionKey;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.command.StaffLoginRequest;
import org.ecnusmartboys.application.dto.request.command.WxLoginRequest;
import org.ecnusmartboys.application.dto.request.command.WxRegisterRequest;
import org.ecnusmartboys.application.dto.response.Response;
import org.ecnusmartboys.application.service.AuthService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Api(tags = "用户接口")
public class AuthController {

    private final AuthService authService;

    @AnonymousAccess
    @ApiOperation("微信登录")
    @PostMapping("/login-wx")
    public Response<UserInfo> loginWx(@RequestBody @Validated WxLoginRequest req, HttpServletRequest request) {
        var res = authService.loginWx(req);
        var u = res.getData();
        var session = request.getSession();
        session.setAttribute(SessionKey.UserID, u.getId());
        session.setAttribute(SessionKey.Role, "visitor");
        return res;
    }

    @AnonymousAccess
    @ApiOperation("访客注册")
    @PostMapping("/register")
    @Transactional
    public Response<UserInfo> register(@RequestBody @Validated WxRegisterRequest req, HttpServletRequest request) {
        var res = authService.register(req);
        var u = res.getData();
        var session = request.getSession();
        session.setAttribute(SessionKey.UserID, u.getId());
        session.setAttribute(SessionKey.Role, "visitor");
        return res;
    }

    @AnonymousAccess
    @ApiOperation("员工登录")
    @PostMapping("/login-staff")
    public Response<UserInfo> staffLogin(@RequestBody @Validated StaffLoginRequest req, HttpServletRequest request) {
        var res = authService.staffLogin(req);
        var user = res.getData();
        // 保存登录信息
        var session = request.getSession();
        session.setAttribute(SessionKey.UserID, user.getId());
        session.setAttribute(SessionKey.Role, req.getRole());
        return res;
    }

    @AnonymousAccess
    @ApiOperation("登出")
    @PostMapping("/logout")
    public Response<Object> logout(HttpServletRequest request) {
        var session = request.getSession();
        session.invalidate();
        return Response.ok();
    }
}
