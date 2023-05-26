package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.request.query.UserListReq;
import org.ecnusmartboys.application.dto.response.ConsultantsResponse;
import org.ecnusmartboys.application.dto.response.Response;


public interface UserArrangeService {
    Response<ConsultantsResponse> getConsultants(UserListReq req);
}
