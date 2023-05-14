package org.ecnusmartboys.api.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.annotation.AnonymousAccess;
import org.ecnusmartboys.infrastructure.exception.ForbiddenException;
import org.ecnusmartboys.infrastructure.mapper.UserInfoMapper;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.infrastructure.model.mysql.User;
import org.ecnusmartboys.infrastructure.model.mysql.Visitor;
import org.ecnusmartboys.application.dto.request.command.StaffLoginReq;
import org.ecnusmartboys.application.dto.request.command.WxLoginReq;
import org.ecnusmartboys.application.dto.request.command.WxRegisterReq;
import org.ecnusmartboys.application.dto.response.Response;
import org.ecnusmartboys.domain.repository.StaffRepository;
import org.ecnusmartboys.domain.repository.VisitorRepository;
import org.ecnusmartboys.domain.service.UserService;
import org.ecnusmartboys.infrastructure.utils.CaptchaUtil;
import org.ecnusmartboys.infrastructure.utils.SmsUtil;
import org.ecnusmartboys.infrastructure.utils.WxUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.ecnusmartboys.domain.service.UserService.ROLE_VISITOR;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Api(tags = "用户接口")
public class AuthController {

    private final UserService userService;

    private final CaptchaUtil captchaUtil;

    private final UserInfoMapper userInfoMapper;

    private final StaffRepository staffRepository;

    private final VisitorRepository visitorRepository;

    private final WxUtil wxUtil;

    private final SmsUtil smsUtil;

    @AnonymousAccess
    @ApiOperation("微信登录")
    @PostMapping("/login-wx")
    public Response<UserInfo> loginWx(@RequestBody @Validated WxLoginReq req, HttpServletRequest request) {
        var code2Session = wxUtil.code2Session(req.getCode());
        Assert.isTrue(code2Session != null && StringUtils.hasText(code2Session.getOpenid()), "获取openid失败");
        var u = getByOpenId(code2Session.getOpenid());
        if (u != null) {
            if (Boolean.TRUE.equals(u.getDisabled())) {
                throw new ForbiddenException("用户已被禁用");
            }
            var session = request.getSession();
            session.setAttribute("userId", u.getId());
            session.setAttribute("roles", u.getRoles());
        }

        return Response.ok(userInfoMapper.toDto(u));
    }

    @AnonymousAccess
    @ApiOperation("访客注册")
    @PostMapping("/register")
    @Transactional
    public Response<UserInfo> register(@RequestBody @Validated WxRegisterReq req, HttpServletRequest request) {
        var validSms = smsUtil.verifyCode(req.getPhone(), req.getSmsCodeId(), req.getSmsCode());
        Assert.isTrue(validSms, "短信验证码错误");

        var u = saveVisitor(req);

        var session = request.getSession();
        session.setAttribute("userId", u.getId());
        session.setAttribute("roles", u.getRoles());

        return Response.ok(userInfoMapper.toDto(u));
    }

    @AnonymousAccess
    @ApiOperation("员工登录")
    @PostMapping("/login-staff")
    public Response<UserInfo> staffLogin(@RequestBody @Validated StaffLoginReq req, HttpServletRequest request) {
        // 验证登录
        var validCaptcha = captchaUtil.verifyCaptcha(req.getCaptchaId(), req.getCaptcha());
        Assert.isTrue(validCaptcha, "验证码错误");

        var wrapper = new QueryWrapper<User>().eq("username", req.getUsername());
        var user = userService.getOne(wrapper);
        Assert.notNull(user, "用户名或密码错误");

        var validpw = BCrypt.checkpw(req.getPassword(), user.getPassword());
        Assert.isTrue(validpw, "用户名或密码错误");
        if (Boolean.TRUE.equals(user.getDisabled())) {
            throw new ForbiddenException("用户已被禁用");
        }

        // 保存登录信息
        var session = request.getSession();
        session.setAttribute("userId", user.getId());
        session.setAttribute("roles", user.getRoles());

        var result = userInfoMapper.toDto(user);
        result.setStaff(staffRepository.selectById(user.getId()));
        return Response.ok(result);
    }

    @AnonymousAccess
    @ApiOperation("登出")
    @PostMapping("/logout")
    public Response<Object> logout(HttpServletRequest request) {
        var session = request.getSession();
        session.invalidate();
        return Response.ok();
    }


    public User saveVisitor(WxRegisterReq req) {
        User user = new User();
        BeanUtils.copyProperties(req, user);
        user.setRoles(Collections.singletonList(ROLE_VISITOR));
        var c = wxUtil.code2Session(req.getCode());
        user.setDisabled(false);
        user.setOpenId(c.getOpenid());
        userService.save(user);

        Visitor visitor = new Visitor();
        visitor.setId(user.getId());
        visitor.setEmergencyContact(req.getEmergencyContact());
        visitor.setEmergencyPhone(req.getEmergencyPhone());
        visitorRepository.insert(visitor);

        return user;
    }

    private User getByOpenId(String phone) {
        var wrapper = new QueryWrapper<User>().eq("open_id", phone);
        return userService.getOne(wrapper);
    }
}
