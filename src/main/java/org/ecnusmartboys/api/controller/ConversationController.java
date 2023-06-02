package org.ecnusmartboys.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.Extractor;
import org.ecnusmartboys.api.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.request.command.StartConsultRequest;
import org.ecnusmartboys.application.dto.request.query.ConsultRecordListReq;
import org.ecnusmartboys.application.dto.response.*;
import org.ecnusmartboys.application.service.ConversationService;
import org.ecnusmartboys.domain.model.user.Admin;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.domain.model.conversation.ConversationInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/conversation")
@Api(tags = "会话接口")
public class ConversationController {

    private final ConversationService conversationService;

    @AuthRoles(Visitor.ROLE)
    @PostMapping("/consult")
    @ApiOperation("发起咨询会话")
    public Responses<StartConsultResponse> startConversation(@RequestBody @Validated StartConsultRequest req, HttpServletRequest request){
        var common = Extractor.extract(request);
        return Responses.ok();
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("管理员获得咨询记录列表")
    @GetMapping("/admin/consultations")
    public Responses<ConsultRecordsResponse> getAllConsultations(@Validated ConsultRecordListReq req){
        return conversationService.getAllConsultations(req);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("管理员获得今日咨询信息")
    @GetMapping("/todayConsultations")
    public Responses<List<ConversationInfo>> getTodayConsultations() {
        return conversationService.getTodayConsultations();
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("管理员获得最近七日咨询信息")
    @GetMapping("/recentWeek")
    public Responses<List<DayConsultInfo>> getRecentWeek() {
        return conversationService.getRecentWeek();
    }

    @AuthRoles(Consultant.ROLE)
    @ApiOperation("咨询师获得咨询记录列表")
    @GetMapping("/consultant/consultants")
    public Responses<ConsultRecordsResponse> getConsultantConsultations(@Validated ConsultRecordListReq req, HttpServletRequest request){
        var common = Extractor.extract(request);
        return conversationService.getConsultConsultations(req, common);
    }

    @AuthRoles(Consultant.ROLE)
    @ApiOperation("咨询师获得最近咨询记录")
    @GetMapping("/consultant/recentConsultations")
    public Responses<ConsultRecordsResponse> getRecentConsultations(HttpServletRequest request){
        var common = Extractor.extract(request);
        return conversationService.getRecentConsultations(common);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("咨询师获得今日咨询信息")
    @GetMapping("/consultant/todayConsultations")
    public Responses<List<ConversationInfo>> getTodayOwnConsultations(HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getTodayOwnConsultations(common);
    }

    @AuthRoles(Supervisor.ROLE)
    @ApiOperation("督导获得求助记录列表")
    @GetMapping("/supervisor/helpRecords")
    public Responses<HelpRecordsResponse> getSupervisorHelpRecords(@Validated ConsultRecordListReq req, HttpServletRequest request){
        var common = Extractor.extract(request);
        return conversationService.getSupervisorHelpRecords(req, common);
    }

    @AuthRoles(Supervisor.ROLE)
    @ApiOperation("督导获得最近咨询记录")
    @GetMapping("/supervisor/recentHelps")
    public Responses<HelpRecordsResponse> getRecentHelps(HttpServletRequest request){
        var common = Extractor.extract(request);
        return conversationService.getRecentHelps(common);
    }

    @AuthRoles(Supervisor.ROLE)
    @ApiOperation("督导获得今日的求助信息")
    @GetMapping("/supervisor/todayHelps")
    public Responses<List<ConversationInfo>> getTodayHelps(HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getTodayHelps(common);
    }
}
