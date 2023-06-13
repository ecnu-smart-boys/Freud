package org.ecnusmartboys.application.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.convertor.WxRegisterReqConvertor;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.StaffLoginRequest;
import org.ecnusmartboys.application.dto.request.command.WxLoginRequest;
import org.ecnusmartboys.application.dto.request.command.WxRegisterRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.AuthService;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.domain.repository.OnlineUserRepository;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.ecnusmartboys.infrastructure.exception.BusinessException;
import org.ecnusmartboys.infrastructure.exception.ForbiddenException;
import org.ecnusmartboys.application.convertor.UserInfoConvertor;
import org.ecnusmartboys.infrastructure.exception.UnauthorizedException;
import org.ecnusmartboys.infrastructure.utils.CaptchaUtil;
import org.ecnusmartboys.infrastructure.utils.SmsUtil;
import org.ecnusmartboys.infrastructure.utils.WxUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final OnlineUserRepository onlineUserRepository;

    private final CaptchaUtil captchaUtil;

    private final UserInfoConvertor userInfoConvertor;
    private final WxRegisterReqConvertor wxRegisterReqConvertor;

    private final WxUtil wxUtil;

    private final SmsUtil smsUtil;

    @Override
    public Responses<UserInfo> loginWx(WxLoginRequest req) {
        var code2Session = wxUtil.code2Session(req.getCode());
        Assert.isTrue(code2Session != null && StringUtils.hasText(code2Session.getOpenid()), "获取openid失败");
        Visitor user;
        if(userRepository.retrieveByOpenId(code2Session.getOpenid()) instanceof Visitor tmp){
            if(tmp.isDisabled()){
                throw ForbiddenException.DISABLED;
            }
            user = tmp;
            onlineUserRepository.join(user);
        }else{
            throw UnauthorizedException.AUTHENTICATION_FAIL;
        }
        return Responses.ok(userInfoConvertor.fromEntity(user));
    }

    @Override
    public Responses<UserInfo> register(WxRegisterRequest req) {
        //var validSms = smsUtil.verifyCode(req.getPhone(), req.getSmsCodeId(), req.getSmsCode());
        //Assert.isTrue(validSms, "短信验证码错误");

        Visitor visitor = wxRegisterReqConvertor.toEntity(req);
        visitor.setOpenID(wxUtil.code2Session(req.getCode()).getOpenid());
        try {
            userRepository.save(visitor);
        } catch (DuplicateKeyException e) {
            throw new BadRequestException("用户已注册");
        }

        return Responses.ok(userInfoConvertor.fromEntity(visitor));
    }

    @Override
    public Responses<UserInfo> staffLogin(StaffLoginRequest req) {
        // 验证登录
        var validCaptcha = captchaUtil.verifyCaptcha(req.getCaptchaId(), req.getCaptcha());
        if (!validCaptcha) {
            throw new UnauthorizedException("验证码错误");
        }

        var user = userRepository.retrieveByUsername(req.getUsername());
        if (user == null) {
            throw UnauthorizedException.AUTHENTICATION_FAIL;
        }

//        var validpw = user.getPassword().equals(req.getPassword());
//        if (!validpw) {
//            throw UnauthorizedException.AUTHENTICATION_FAIL;
//        }
        if (Boolean.TRUE.equals(user.isDisabled())) {
            throw ForbiddenException.DISABLED;
        }
        // TODO 今日是否排班
        onlineUserRepository.join(user);
        var result = userInfoConvertor.fromEntity(user);
        return Responses.ok(result);
    }

    @Override
    public void logout(Common common) {
        onlineUserRepository.logout(common.getUserId());
    }

}
