package org.ecnusmartboys.application.service.impl;

import io.github.doocs.im.ImClient;
import io.github.doocs.im.model.request.KickRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.convertor.UpdateVisitorReqConvertor;
import org.ecnusmartboys.application.convertor.UserInfoConvertor;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.enums.OnlineState;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.UpdateVisitorRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.OnlineStateService;
import org.ecnusmartboys.application.service.UserService;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.config.IMConfig;
import org.ecnusmartboys.infrastructure.exception.InternalException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserInfoConvertor userInfoConvertor;
    private final UpdateVisitorReqConvertor updateVisitorReqConvertor;
    private final OnlineStateService onlineStateService;
    private final ImClient adminClient;

    @Override
    public Responses<UserInfo> getUserInfo(Common common) {
        var user = userRepository.retrieveById(common.getUserId());
        if(user == null) {
            return Responses.ok(null);
        }

        var userInfo = userInfoConvertor.fromEntity(user);
        return Responses.ok(userInfo);
    }

    @Override
    @Transactional
    public Responses<Object> updateVisitorInfo(UpdateVisitorRequest req, Common common) {
        var visitor = updateVisitorReqConvertor.toEntity(req);
        visitor.setId(common.getUserId());
        userRepository.save(visitor);
        return null;
    }

    @Override
    public void offline(Long userId) {
        onlineStateService.setUserState(userId, OnlineState.OFFLINE);
        var kickRequest = KickRequest.builder().userId(userId.toString()).build();
        try {
            adminClient.account.kick(kickRequest);
        } catch (IOException e) {
            log.error("IM踢下线失败, userId {}, {}", userId, e.getMessage());
        }
    }
}
