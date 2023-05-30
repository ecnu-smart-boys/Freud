package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.DayArrangeInfo;
import org.ecnusmartboys.application.dto.StaffBaseInfo;
import org.ecnusmartboys.application.dto.request.command.AddArrangementRequest;
import org.ecnusmartboys.application.dto.request.command.RemoveArrangeRequest;
import org.ecnusmartboys.application.dto.request.query.GetMonthArrangementRequest;
import org.ecnusmartboys.application.dto.request.query.NoArrangedRequest;
import org.ecnusmartboys.application.dto.response.Responses;

import java.util.Date;
import java.util.List;

public interface ArrangeService {
    Responses<Object> remove(RemoveArrangeRequest req);

    Responses<Object> addArrangement(AddArrangementRequest req);

    Responses<List<StaffBaseInfo>> getConsultants(Long timestamp);

    Responses<List<StaffBaseInfo>> getSupervisors(Long timestamp);

    Responses<List<DayArrangeInfo>> getMonthArrangement(GetMonthArrangementRequest req);

    Responses<List<StaffBaseInfo>> getNotArranged(NoArrangedRequest req, String role);
}
