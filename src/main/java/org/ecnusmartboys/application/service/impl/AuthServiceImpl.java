package org.ecnusmartboys.application.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.convertor.WxRegisterReqConvertor;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.command.StaffLoginRequest;
import org.ecnusmartboys.application.dto.request.command.WxLoginRequest;
import org.ecnusmartboys.application.dto.request.command.WxRegisterRequest;
import org.ecnusmartboys.application.dto.response.Response;
import org.ecnusmartboys.application.service.AuthService;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.exception.ForbiddenException;
import org.ecnusmartboys.application.convertor.UserInfoConvertor;
import org.ecnusmartboys.infrastructure.exception.UnauthorizedException;
import org.ecnusmartboys.infrastructure.utils.CaptchaUtil;
import org.ecnusmartboys.infrastructure.utils.SmsUtil;
import org.ecnusmartboys.infrastructure.utils.WxUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private final CaptchaUtil captchaUtil;

    private final UserInfoConvertor userInfoConvertor;
    private final WxRegisterReqConvertor wxRegisterReqConvertor;

    private final WxUtil wxUtil;

    private final SmsUtil smsUtil;

    @Override
    public Response<UserInfo> loginWx(WxLoginRequest req) {
        var code2Session = wxUtil.code2Session(req.getCode());
        Assert.isTrue(code2Session != null && StringUtils.hasText(code2Session.getOpenid()), "获取openid失败");
        var u = (Visitor)userRepository.retrieveByOpenId(code2Session.getOpenid(), "visitor");
        if (u != null && Boolean.TRUE.equals(u.isDisabled())) {
            throw ForbiddenException.DISABLED;
        }

        return Response.ok(userInfoConvertor.fromEntity(u));
    }

    @Override
    public Response<UserInfo> register(WxRegisterRequest req) {
        var validSms = smsUtil.verifyCode(req.getPhone(), req.getSmsCodeId(), req.getSmsCode());
        Assert.isTrue(validSms, "短信验证码错误");

        Visitor visitor = wxRegisterReqConvertor.toEntity(req);
        visitor.setOpenID(wxUtil.code2Session(req.getCode()).getOpenid());
        userRepository.save(visitor);


        return Response.ok(userInfoConvertor.fromEntity(visitor));
    }

    @Override
    public Response<UserInfo> staffLogin(StaffLoginRequest req) {
        // 验证登录
        var validCaptcha = captchaUtil.verifyCaptcha(req.getCaptchaId(), req.getCaptcha());
        if (!validCaptcha) {
            throw UnauthorizedException.AUTHENTICATION_FAIL;
        }

        var user = (Consultant)userRepository.retrieveByName(req.getUsername(), "staff");
        if (user == null) {
            throw UnauthorizedException.AUTHENTICATION_FAIL;
        }

        var validpw = BCrypt.checkpw(req.getPassword(), user.getPassword());
        if (!validpw) {
            throw UnauthorizedException.AUTHENTICATION_FAIL;
        }
        if (Boolean.TRUE.equals(user.isDisabled())) {
            throw ForbiddenException.DISABLED;
        }
        var result = userInfoConvertor.fromEntity(user);
        return Response.ok(result);
    }

}