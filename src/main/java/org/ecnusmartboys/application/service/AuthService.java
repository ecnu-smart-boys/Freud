package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.command.StaffLoginRequest;
import org.ecnusmartboys.application.dto.request.command.WxLoginRequest;
import org.ecnusmartboys.application.dto.request.command.WxRegisterRequest;
import org.ecnusmartboys.application.dto.response.Response;

public interface AuthService {
    Response<UserInfo> loginWx(WxLoginRequest req);
    Response<UserInfo> register(WxRegisterRequest req);
    Response<UserInfo> staffLogin(StaffLoginRequest req);
}
