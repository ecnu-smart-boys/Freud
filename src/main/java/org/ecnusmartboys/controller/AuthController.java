package org.ecnusmartboys.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.annotation.AnonymousAccess;
import org.ecnusmartboys.exception.ForbiddenException;
import org.ecnusmartboys.mapstruct.UserInfoMapper;
import org.ecnusmartboys.model.dto.UserInfo;
import org.ecnusmartboys.model.entity.User;
import org.ecnusmartboys.model.entity.Visitor;
import org.ecnusmartboys.model.request.StaffLoginReq;
import org.ecnusmartboys.model.request.WxLoginReq;
import org.ecnusmartboys.model.request.WxRegisterReq;
import org.ecnusmartboys.model.response.BaseResponse;
import org.ecnusmartboys.repository.StaffRepository;
import org.ecnusmartboys.repository.VisitorRepository;
import org.ecnusmartboys.service.UserService;
import org.ecnusmartboys.utils.CaptchaUtil;
import org.ecnusmartboys.utils.SmsUtil;
import org.ecnusmartboys.utils.WxUtil;
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

import static org.ecnusmartboys.service.UserService.ROLE_VISITOR;

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
    public BaseResponse<UserInfo> loginWx(@RequestBody @Validated WxLoginReq req, HttpServletRequest request) {
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

        return BaseResponse.ok(userInfoMapper.toDto(u));
    }

    @AnonymousAccess
    @ApiOperation("访客注册")
    @PostMapping("/register")
    @Transactional
    public BaseResponse<UserInfo> register(@RequestBody @Validated WxRegisterReq req, HttpServletRequest request) {
        var validSms = smsUtil.verifyCode(req.getPhone(), req.getSmsCodeId(), req.getSmsCode());
        Assert.isTrue(validSms, "短信验证码错误");

        var u = saveVisitor(req);

        var session = request.getSession();
        session.setAttribute("userId", u.getId());
        session.setAttribute("roles", u.getRoles());

        return BaseResponse.ok(userInfoMapper.toDto(u));
    }

    @AnonymousAccess
    @ApiOperation("员工登录")
    @PostMapping("/login-staff")
    public BaseResponse<UserInfo> staffLogin(@RequestBody @Validated StaffLoginReq req, HttpServletRequest request) {
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
        return BaseResponse.ok(result);
    }

    @AnonymousAccess
    @ApiOperation("登出")
    @PostMapping("/logout")
    public BaseResponse<Object> logout(HttpServletRequest request) {
        var session = request.getSession();
        session.invalidate();
        return BaseResponse.ok();
    }


    public User saveVisitor(WxRegisterReq req) {
        User user = new User();
        BeanUtils.copyProperties(req, user);
        user.setRoles(Collections.singletonList(ROLE_VISITOR));
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
