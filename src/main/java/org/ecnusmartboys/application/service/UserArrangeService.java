package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.request.query.UserListReq;
import org.ecnusmartboys.application.dto.response.ConsultantsResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.dto.response.SupervisorsResponse;
import org.ecnusmartboys.application.dto.response.VisitorsResponse;


public interface UserArrangeService {
    Responses<ConsultantsResponse> getConsultants(UserListReq req);
    Responses<SupervisorsResponse> getSupervisors(UserListReq req);
    Responses<VisitorsResponse> getVisitors(UserListReq req);
}
