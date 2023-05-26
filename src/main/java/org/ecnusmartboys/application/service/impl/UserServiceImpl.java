package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.UpdateUserInfoRequest;
import org.ecnusmartboys.application.dto.response.Response;
import org.ecnusmartboys.application.service.UserService;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Response<UserInfo> getUserInfo(Common common) {
        var user = userRepository.retrieveByUserId(common.getUserId());
        return null;
    }

    @Override
    public Response<Object> updateUserInfo(UpdateUserInfoRequest req) {
        return null;
    }
}
