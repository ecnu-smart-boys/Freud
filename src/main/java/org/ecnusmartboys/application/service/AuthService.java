package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.StaffLoginRequest;
import org.ecnusmartboys.application.dto.request.command.WxLoginRequest;
import org.ecnusmartboys.application.dto.request.command.WxRegisterRequest;
import org.ecnusmartboys.application.dto.response.Responses;

public interface AuthService {
    Responses<UserInfo> loginWx(WxLoginRequest req);

    Responses<UserInfo> register(WxRegisterRequest req);

    Responses<UserInfo> staffLogin(StaffLoginRequest req);

    void logout(Common common);
}
