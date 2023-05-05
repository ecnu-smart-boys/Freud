package org.ecnusmartboys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.annotation.AnonymousAccess;
import org.ecnusmartboys.config.IMConfig;
import org.ecnusmartboys.exception.ForbiddenException;
import org.ecnusmartboys.mapstruct.UserInfoMapper;
import org.ecnusmartboys.model.dto.UserInfo;
import org.ecnusmartboys.model.entity.User;
import org.ecnusmartboys.model.entity.Visitor;
import org.ecnusmartboys.model.request.WxLoginReq;
import org.ecnusmartboys.model.request.WxRegisterReq;
import org.ecnusmartboys.model.response.BaseResponse;
import org.ecnusmartboys.repository.VisitorRepository;
import org.ecnusmartboys.service.UserService;
import org.ecnusmartboys.utils.SmsUtil;
import org.ecnusmartboys.utils.Validator;
import org.ecnusmartboys.utils.WxUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.ecnusmartboys.service.UserService.ROLE_VISITOR;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/wx")
@Api(tags = "微信用户接口")
public class WxController {

    private final UserService userService;

    private final VisitorRepository visitorRepository;

    private final WxUtil wxUtil;

    private final UserInfoMapper userInfoMapper;

    private final SmsUtil smsUtil;

    @AnonymousAccess
    @ApiOperation("微信登录")
    @PostMapping("/login")
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
        Assert.isTrue(Validator.validatePhone(req.getPhone()), "手机号格式不正确");
        Assert.isTrue(Validator.validatePhone(req.getEmergencyPhone()), "紧急联系人手机号格式不正确");

        var validSms = smsUtil.verifyCode(req.getPhone(), req.getSmsCodeId(), req.getSmsCode());
        if (!validSms) {
            throw new ForbiddenException("短信验证码错误");
        }

        var u = saveVisitor(req);

        var session = request.getSession();
        session.setAttribute("userId", u.getId());
        session.setAttribute("roles", u.getRoles());

        return BaseResponse.ok(userInfoMapper.toDto(u));
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
        var wrapper = new QueryWrapper<User>();
        wrapper.eq("open_id", phone);
        return userService.getOne(wrapper);
    }

}
