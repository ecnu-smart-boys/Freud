package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.AvailableConsultant;
import org.ecnusmartboys.application.dto.StaffBaseInfo;
import org.ecnusmartboys.application.dto.conversation.LeftConversation;
import org.ecnusmartboys.application.dto.conversation.WxConsultRecordInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.ConsultRecordListReq;
import org.ecnusmartboys.application.dto.request.query.OnlineStaffListRequest;
import org.ecnusmartboys.application.dto.response.*;
import org.ecnusmartboys.domain.model.conversation.ConversationInfo;

import java.util.List;

public interface ConversationService {

    Responses<ConsultRecordsResponse> getAllConsultations(ConsultRecordListReq req);

    Responses<ConsultRecordsResponse> getConsultConsultations(ConsultRecordListReq req, Common common);

    Responses<HelpRecordsResponse> getSupervisorHelpRecords(ConsultRecordListReq req, Common common);

    Responses<List<WxConsultRecordInfo>> getVisitorConsultations(Common common);

    Responses<List<ConversationInfo>> getTodayConsultations();

    Responses<Integer> getAvgComment(Common common);

    Responses<List<DayConsultInfo>> getRecentWeek();

    Responses<List<ConversationInfo>> getTodayOwnConsultations(Common common);

    Responses<ConsultRecordsResponse> getRecentConsultations(Common common);

    Responses<ConsultRecordsResponse> getBoundConsultations(ConsultRecordListReq req, Common common);

    Responses<HelpRecordsResponse> getRecentHelps(Common common);

    Responses<List<ConversationInfo>> getTodayHelps(Common common);

    Responses<LeftConversation> startConversation(StartConsultRequest req, Common common);

    Responses<EndConsultResponse> endConsultation(EndConsultRequest req, Common common);

    Responses<List<StaffBaseInfo>> getAvailableSupervisors(Common common);

    Responses<Object> callHelp(CallHelpRequest req, Common common);

    Responses<Object> endHelp(EndHelpRequest req, Common common);

    Responses<Object> cancelWaiting(String userId);

    Responses<EndConsultResponse> visitorComment(VisitorCommentRequest req, Common common);

    Responses<EndConsultResponse> consultantComment(ConsultantCommentRequest req, Common common);

    Responses<Object> setting(SettingRequest req, Common common);

    Responses<RankResponse> getRank();

    Responses<Integer> getMaxConversations(Common common);

    Responses<OnlineInfoResponse> getOnlineConsultantInfo(OnlineStaffListRequest req);

    Responses<OnlineInfoResponse> getOnlineSupervisorInfo(OnlineStaffListRequest req);

    Responses<OnlineInfoResponse> getOnlineBoundConsultantInfo(OnlineStaffListRequest req, Common common);

    Responses<Integer> getOnlineConversationNumber(Common common);

    Responses<List<AvailableConsultant>> getAvailableConsultants(Common common);

    Responses<WebConversationInfoResponse> getSupervisorOwnHelpInfo(String helpId, Common common);

    Responses<WebConversationInfoResponse> getBoundConsultantsInfo(String conversationId, Common common);

    Responses<WebConversationInfoResponse> getConsultantOwnConsultationInfo(String conversationId, Common common);

    Responses<WebConversationInfoResponse> getAdminConsultationInfo(String conversationId, Common common);

    Responses<List<LeftConversation>> getConversationsList(Common common);

    Responses<WebConversationInfoResponse> getConsultationInfo(String conversationId, Common common);

    Responses<WebConversationInfoResponse> getHelpInfo(String conversationId, Common common);

    Responses<Object> removeConversation(RemoveConversationRequest req, Common common);


    Responses<OnlineStateResponse> getOnlineVisitorState(Common common);
}
