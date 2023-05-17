package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.command.StaffLoginReq;
import org.ecnusmartboys.application.dto.request.command.WxLoginReq;
import org.ecnusmartboys.application.dto.request.command.WxRegisterReq;
import org.ecnusmartboys.application.dto.response.Response;

public interface AuthService {
    Response<UserInfo> loginWx(WxLoginReq req);
    Response<UserInfo> register(WxRegisterReq req);
    Response<UserInfo> staffLogin(StaffLoginReq req);
}
