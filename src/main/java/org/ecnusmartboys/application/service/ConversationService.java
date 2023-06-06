package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.ConsultRecordListReq;
import org.ecnusmartboys.application.dto.response.*;
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

    Responses<StartConsultResponse> startConversation(StartConsultRequest req, Common common);

    Responses<EndConsultResponse> endConsultation(EndConsultRequest req, Common common);

    Responses<Object> callHelp(CallHelpRequest req, Common common);

    Responses<Object> endHelp(EndHelpRequest req, Common common);

    Responses<EndConsultResponse> comment(CommentRequest req, Common common);

    Responses<Object> probeConsultation(ProbeRequest req, Common common);

    Responses<Object> probeHelp(ProbeRequest req, Common common);

    Responses<Object> setting(SettingRequest req, Common common);
}
