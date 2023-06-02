package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.query.ConsultRecordListReq;
import org.ecnusmartboys.application.dto.response.ConsultRecordsResponse;
import org.ecnusmartboys.application.dto.response.DayConsultInfo;
import org.ecnusmartboys.application.dto.response.HelpRecordsResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.domain.model.conversation.ConversationInfo;

import java.util.List;

public interface ConversationService {

    Responses<ConsultRecordsResponse> getAllConsultations(ConsultRecordListReq req);

    Responses<ConsultRecordsResponse> getConsultConsultations(ConsultRecordListReq req, Common common);

    Responses<HelpRecordsResponse> getSupervisorHelpRecords(ConsultRecordListReq req, Common common);

    Responses<List<ConversationInfo>> getTodayConsultations();

    Responses<List<DayConsultInfo>> getRecentWeek();

    Responses<List<ConversationInfo>> getTodayOwnConsultations(Common common);

    Responses<ConsultRecordsResponse> getRecentConsultations(Common common);

    Responses<HelpRecordsResponse> getRecentHelps(Common common);

    Responses<List<ConversationInfo>> getTodayHelps(Common common);
}
