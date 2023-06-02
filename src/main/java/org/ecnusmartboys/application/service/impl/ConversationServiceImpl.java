package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.dto.ConsultRecordInfo;
import org.ecnusmartboys.application.dto.HelpRecordInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.query.ConsultRecordListReq;
import org.ecnusmartboys.application.dto.response.ConsultRecordsResponse;
import org.ecnusmartboys.application.dto.response.DayConsultInfo;
import org.ecnusmartboys.application.dto.response.HelpRecordsResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.ConversationService;
import org.ecnusmartboys.domain.model.conversation.Conversation;
import org.ecnusmartboys.domain.repository.ConversationRepository;
import org.ecnusmartboys.domain.model.conversation.ConversationInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ConversationServiceImpl implements ConversationService {

    private final static long ONE_DAY = 24 * 60 * 60 * 1000L;

    private final ConversationRepository conversationRepository;

    @Override
    public Responses<ConsultRecordsResponse> getAllConsultations(ConsultRecordListReq req) {
        var pageResult =  conversationRepository.retrieveAllConsultations(req.getCurrent(), req.getSize(), req.getName(), req.getTimestamp());
        var records = convertToConsultationList(pageResult.getData());
        return Responses.ok(new ConsultRecordsResponse(records, pageResult.getTotal()));
    }

    @Override
    public Responses<ConsultRecordsResponse> getConsultConsultations(ConsultRecordListReq req, Common common) {
        var pageResult =  conversationRepository.retrieveConsultationsByToUser(req.getCurrent(), req.getSize(), req.getName(), req.getTimestamp(), common.getUserId());
        var records = convertToConsultationList(pageResult.getData());
        return Responses.ok(new ConsultRecordsResponse(records, pageResult.getTotal()));
    }

    @Override
    public Responses<HelpRecordsResponse> getSupervisorHelpRecords(ConsultRecordListReq req, Common common) {
        var pageResult = conversationRepository.retrieveConsultationsByToUser(req.getCurrent(), req.getSize(), req.getName(), req.getTimestamp(), common.getUserId());
        var records = convertToHelpList(pageResult.getData());
        return Responses.ok(new HelpRecordsResponse(records, pageResult.getTotal()));
    }

    @Override
    public Responses<List<ConversationInfo>> getTodayConsultations() {
        List<ConversationInfo> result = conversationRepository.retrieveByDate(new Date());
        return Responses.ok(result);
    }

    @Override
    public Responses<List<DayConsultInfo>> getRecentWeek() {
        long currentDay = (new Date().getTime()) - 6 * ONE_DAY;
        List<DayConsultInfo> dayConsultInfos = new ArrayList<>();

        for(int i = 0; i < 7; i++, currentDay += ONE_DAY) {
            List<ConversationInfo> result = conversationRepository.retrieveByDate(new Date(currentDay));
            dayConsultInfos.add(new DayConsultInfo(currentDay, result.size()));
        }
        return Responses.ok(dayConsultInfos);
    }

    @Override
    public Responses<List<ConversationInfo>> getTodayOwnConsultations(Common common) {
        List<ConversationInfo> result = conversationRepository.retrieveByDateAndToId(new Date(), common.getUserId());
        return Responses.ok(result);
    }

    @Override
    public Responses<ConsultRecordsResponse> getRecentConsultations(Common common) {
        var conversations = conversationRepository.retrieveRecent(common.getUserId());
        var records = convertToConsultationList(conversations);
        return Responses.ok(new ConsultRecordsResponse(records, conversations.size()));
    }

    @Override
    public Responses<HelpRecordsResponse> getRecentHelps(Common common) {
        var conversations = conversationRepository.retrieveRecent(common.getUserId());
        var records = convertToHelpList(conversations);
        return Responses.ok(new HelpRecordsResponse(records, records.size()));
    }

    @Override
    public Responses<List<ConversationInfo>> getTodayHelps(Common common) {
        List<ConversationInfo> result = conversationRepository.retrieveByDateAndToId(new Date(), common.getUserId());
        return Responses.ok(result);
    }

    @NotNull
    private List<ConsultRecordInfo> convertToConsultationList(List<Conversation> conversations) {
        List<ConsultRecordInfo> records = new ArrayList<>();

        conversations.forEach(conversation -> {
            ConsultRecordInfo recordInfo = new ConsultRecordInfo();

            recordInfo.setId(conversation.getId());
            recordInfo.setVisitorName(conversation.getFromUser().getName());
            recordInfo.setConsultantName(conversation.getToUser().getName());
            recordInfo.setScore(conversation.getFromUserComment().getScore());
            recordInfo.setComment(conversation.getFromUserComment().getText());
            recordInfo.setStartTime(conversation.getStartTime());
            recordInfo.setEndTime(conversation.getEndTime());

            if(conversation.getHelper() != null) {
                recordInfo.setHelper(conversation.getHelper().getSupervisor().getName());
            }

            records.add(recordInfo);
        });

        return records;
    }

    private List<HelpRecordInfo> convertToHelpList(List<Conversation> conversations) {
        List<HelpRecordInfo> records = new ArrayList<>();

        conversations.forEach(conversation -> {
            HelpRecordInfo recordInfo = new HelpRecordInfo();

            recordInfo.setId(conversation.getId());
            recordInfo.setConsultantName(conversation.getFromUser().getName());
            recordInfo.setSupervisorName(conversation.getToUser().getName());
            recordInfo.setStartTime(conversation.getStartTime());
            recordInfo.setEndTime(conversation.getEndTime());

            records.add(recordInfo);
        });

        return records;
    }
}
