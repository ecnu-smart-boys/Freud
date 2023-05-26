package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.UpdateUserInfoRequest;
import org.ecnusmartboys.application.dto.request.command.UpdateVisitorRequest;
import org.ecnusmartboys.application.dto.response.Responses;

public interface UserService {
    Responses<UserInfo> getUserInfo(Common common);

    Responses<Object> updateVisitorInfo(UpdateVisitorRequest req, Common common);
}
