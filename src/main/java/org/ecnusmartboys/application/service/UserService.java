package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.UpdateUserInfoRequest;
import org.ecnusmartboys.application.dto.response.Response;

public interface UserService {
    Response<UserInfo> getUserInfo(Common common);

    Response<Object> updateUserInfo(UpdateUserInfoRequest req);
}
