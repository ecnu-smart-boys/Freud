package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.request.command.AddArrangementRequest;
import org.ecnusmartboys.application.dto.request.command.RemoveArrangeRequest;
import org.ecnusmartboys.application.dto.response.Response;

public interface ArrangeService {
    Response<Object> remove(RemoveArrangeRequest req);
    Response<Object> addConsultant(AddArrangementRequest req);
}
