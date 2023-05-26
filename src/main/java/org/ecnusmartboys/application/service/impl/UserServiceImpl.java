package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.convertor.UpdateVisitorReqConvertor;
import org.ecnusmartboys.application.convertor.UserInfoConvertor;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.UpdateVisitorRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.UserService;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.exception.InternalException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserInfoConvertor userInfoConvertor;
    private final UpdateVisitorReqConvertor updateVisitorReqConvertor;

    @Override
    public Responses<UserInfo> getUserInfo(Common common) {
        var user = userRepository.retrieveById(common.getUserId());
        if(user == null) {
            throw new InternalException("用户不存在");
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
}
