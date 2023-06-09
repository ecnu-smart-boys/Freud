package org.ecnusmartboys.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.Extractor;
import org.ecnusmartboys.api.annotation.AnonymousAccess;
import org.ecnusmartboys.api.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.ConsultRecordListReq;
import org.ecnusmartboys.application.dto.request.query.OnlineStaffListRequest;
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
    @GetMapping("/consultant/consultations")
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
    @ApiOperation("督导获得绑定咨询师的咨询记录")
    @GetMapping("/supervisor/boundConsultations")
    public Responses<ConsultRecordsResponse> getBoundConsultations(@Validated ConsultRecordListReq req, HttpServletRequest request){
        var common = Extractor.extract(request);
        return conversationService.getBoundConsultations(req, common);
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

//    @AuthRoles(Visitor.ROLE)
    @AnonymousAccess
    @PostMapping("/consult")
    @ApiOperation("发起咨询会话")
    public Responses<StartConsultResponse> startConversation(@RequestBody @Validated StartConsultRequest req, HttpServletRequest request){
//        var common = Extractor.extract(request);
        Common common = new Common();
        common.setUserId(req.getMyId());
        return conversationService.startConversation(req, common);
    }

//    @AuthRoles({Visitor.ROLE, Consultant.ROLE})
    @AnonymousAccess
    @PostMapping("/endConsultation")
    @ApiOperation("结束咨询会话")
    public Responses<EndConsultResponse> endConsultation(@RequestBody @Validated EndConsultRequest req, HttpServletRequest request){
//        var common = Extractor.extract(request);
        Common common = new Common();
        common.setUserId(req.getMyId());
        return conversationService.endConsultation(req, common);
    }

//    @AuthRoles(Consultant.ROLE)
    @AnonymousAccess
    @PostMapping("/callHelp")
    @ApiOperation("求助督导")
    public Responses<Object> callHelp(@RequestBody @Validated CallHelpRequest req, HttpServletRequest request) {
//        var common = Extractor.extract(request);
        Common common = new Common();
        common.setUserId(req.getMyId());
        return conversationService.callHelp(req, common);
    }

//    @AuthRoles({Supervisor.ROLE, Consultant.ROLE})
    @AnonymousAccess
    @PostMapping("/endHelp")
    @ApiOperation("结束求助")
    public Responses<Object> endHelp(@RequestBody @Validated EndHelpRequest req, HttpServletRequest request) {
//        var common = Extractor.extract(request);
        Common common = new Common();
        common.setUserId(req.getMyId());
        return conversationService.endHelp(req, common);
    }

//    @AuthRoles(Visitor.ROLE)
    @AnonymousAccess
    @ApiOperation("访客探测排队是否结束")
    @PostMapping("/probeConsultation")
    public Responses<Object> probeConsultation(@RequestBody @Validated ProbeRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.probeConsultation(req, common);
    }

//        @AuthRoles(Consultant.ROLE)
    @AnonymousAccess
    @ApiOperation("咨询师探测排队是否结束")
    @PostMapping("/probeHelp")
    public Responses<Object> probeHelp(@RequestBody @Validated ProbeRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.probeHelp(req, common);
    }


    @AuthRoles({Visitor.ROLE, Consultant.ROLE})
    @PostMapping("/comment")
    @ApiOperation("会话结束后评价")
    public Responses<EndConsultResponse> comment(@RequestBody @Validated CommentRequest req, HttpServletRequest request){
        var common = Extractor.extract(request);
        return conversationService.comment(req, common);
    }

    @AuthRoles({Consultant.ROLE, Supervisor.ROLE})
    @PostMapping("/setting")
    @ApiOperation("会话设置")
    public Responses<Object> setting(@RequestBody @Validated SettingRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.setting(req, common);
    }

    @AuthRoles(Admin.ROLE)
    @GetMapping("/rank")
    @ApiOperation("获得当月咨询数量排行和好评排行")
    public Responses<RankResponse> getRank() {
        return conversationService.getRank();
    }

    @AuthRoles({Consultant.ROLE, Supervisor.ROLE})
    @GetMapping("/maxConversations")
    @ApiOperation("获得在线最大会话数量")
    public Responses<Integer> getMaxConversations(HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getMaxConversations(common);
    }

    @AuthRoles(Admin.ROLE)
    @GetMapping("/onlineConsultantInfo")
    @ApiOperation("管理员获得当前在线咨询师和咨询数量")
    public Responses<OnlineInfoResponse> getOnlineConsultantInfo(@Validated OnlineStaffListRequest req) {
        return conversationService.getOnlineConsultantInfo(req);
    }

    @AuthRoles(Admin.ROLE)
    @GetMapping("/onlineSupervisorInfo")
    @ApiOperation("管理员获得当前在线督导和求助数量")
    public Responses<OnlineInfoResponse> getOnlineSupervisorInfo(@Validated OnlineStaffListRequest req) {
        return conversationService.getOnlineSupervisorInfo(req);
    }

    @AuthRoles(Supervisor.ROLE)
    @GetMapping("/onlineBoundConsultantInfo")
    @ApiOperation("督导获得当前在线且与他绑定的咨询师和咨询数量")
    public Responses<OnlineInfoResponse> getOnlineBoundConsultantInfo(@Validated OnlineStaffListRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getOnlineBoundConsultantInfo(req, common);
    }


}
