package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.UserListReq;
import org.ecnusmartboys.application.dto.response.ConsultantsResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.dto.response.SupervisorsResponse;
import org.ecnusmartboys.application.dto.response.VisitorsResponse;


public interface UserArrangeService {
    Responses<ConsultantsResponse> getConsultants(UserListReq req);
    Responses<SupervisorsResponse> getSupervisors(UserListReq req);
    Responses<VisitorsResponse> getVisitors(UserListReq req);
    Responses<Object> disable(DisableUserRequest request, Common common);
    Responses<Object> enable(EnableUserRequest request, Common common);
    Responses<Object> saveSupervisor(AddSupervisorRequest req);

    Responses<Object> updateSupervisor(UpdateSupervisorRequest req);

    Responses<Object> saveConsultant(AddConsultantRequest req);

    Responses<Object> updateConsultant(UpdateConsultantRequest req);
}
