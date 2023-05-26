package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.request.command.AddArrangementRequest;
import org.ecnusmartboys.application.dto.request.command.RemoveArrangeRequest;
import org.ecnusmartboys.application.dto.response.Responses;

public interface ArrangeService {
    Responses<Object> remove(RemoveArrangeRequest req);
    Responses<Object> addConsultant(AddArrangementRequest req);
}
