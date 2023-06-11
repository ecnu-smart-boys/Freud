package org.ecnusmartboys.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.Extractor;
import org.ecnusmartboys.api.annotation.AnonymousAccess;
import org.ecnusmartboys.api.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.conversation.OnlineConversation;
import org.ecnusmartboys.application.dto.conversation.WxConsultRecordInfo;
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

    /************************* 获得已经结束的会话记录列表接口 *************************/

    @AuthRoles(Admin.ROLE)
    @ApiOperation("管理员获得咨询记录列表")
    @GetMapping("/admin/consultations")
    public Responses<ConsultRecordsResponse> getAllConsultations(@Validated ConsultRecordListReq req){
        return conversationService.getAllConsultations(req);
    }

    @AuthRoles(Consultant.ROLE)
    @ApiOperation("咨询师获得最近咨询记录")
    @GetMapping("/consultant/recentConsultations")
    public Responses<ConsultRecordsResponse> getRecentConsultations(HttpServletRequest request){
        var common = Extractor.extract(request);
        return conversationService.getRecentConsultations(common);
    }

    @AuthRoles(Consultant.ROLE)
    @ApiOperation("咨询师获得咨询记录列表")
    @GetMapping("/consultant/consultations")
    public Responses<ConsultRecordsResponse> getConsultantConsultations(@Validated ConsultRecordListReq req, HttpServletRequest request){
        var common = Extractor.extract(request);
        return conversationService.getConsultConsultations(req, common);
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
    @ApiOperation("督导获得绑定咨询师的咨询记录")
    @GetMapping("/supervisor/boundConsultations")
    public Responses<ConsultRecordsResponse> getBoundConsultations(@Validated ConsultRecordListReq req, HttpServletRequest request){
        var common = Extractor.extract(request);
        return conversationService.getBoundConsultations(req, common);
    }

    @AuthRoles(Visitor.ROLE)
    @ApiOperation("咨询师获得咨询记录列表")
    @GetMapping("/visitor/consultations")
    public Responses<List<WxConsultRecordInfo>> getVisitorConsultations(HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getVisitorConsultations(common);
    }

    /************************* 咨询相关信息，展示在页面首页上 *************************/

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

    @AuthRoles(Admin.ROLE)
    @ApiOperation("咨询师获得今日咨询信息")
    @GetMapping("/consultant/todayConsultations")
    public Responses<List<ConversationInfo>> getTodayOwnConsultations(HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getTodayOwnConsultations(common);
    }

    @AuthRoles(Supervisor.ROLE)
    @ApiOperation("督导获得今日的求助信息")
    @GetMapping("/supervisor/todayHelps")
    public Responses<List<ConversationInfo>> getTodayHelps(HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getTodayHelps(common);
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


    /************************* 进行在线咨询会话 *************************/

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


    @AuthRoles(Visitor.ROLE)
    @PostMapping("/visitorComment")
    @ApiOperation("访客会话结束后评价")
    public Responses<EndConsultResponse> visitorComment(@RequestBody @Validated VisitorCommentRequest req, HttpServletRequest request){
        var common = Extractor.extract(request);
        return conversationService.visitorComment(req, common);
    }

    @AuthRoles(Consultant.ROLE)
    @PostMapping("/consultantComment")
    @ApiOperation("咨询师会话结束后评价")
    public Responses<EndConsultResponse> consultantComment(@RequestBody @Validated ConsultantCommentRequest req, HttpServletRequest request){
        var common = Extractor.extract(request);
        return conversationService.consultantComment(req, common);
    }

    /************************* 查看已经结束的会话详情 *************************/

    @AuthRoles(Supervisor.ROLE)
    @ApiOperation(value = "督导查看自己的求助记录信息")
    @GetMapping("/details/supervisorOwnHelpInfo")
    public Responses<WebConversationInfoResponse> getSupervisorOwnHelpInfo(@RequestParam String helpId, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getSupervisorOwnHelpInfo(helpId, common);
    }

    @AuthRoles(Supervisor.ROLE)
    @ApiOperation(value = "督导查看自己绑定的咨询师的咨询记录信息")
    @GetMapping("/details/boundConsultantsInfo")
    public Responses<WebConversationInfoResponse> getBoundConsultantsInfo(@RequestParam String conversationId, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getBoundConsultantsInfo(conversationId, common);
    }

    @AuthRoles(Consultant.ROLE)
    @ApiOperation(value = "咨询师查看自己的咨询记录信息")
    @GetMapping("/details/consultantOwnConsultationInfo")
    public Responses<WebConversationInfoResponse> getConsultantOwnConsultationInfo(@RequestParam String conversationId, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getConsultantOwnConsultationInfo(conversationId, common);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation(value = "管理员查看咨询记录消息列表")
    @GetMapping("/details/adminConsultationInfo")
    public Responses<WebConversationInfoResponse> getAdminConsultationInfo(@RequestParam String conversationId, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getAdminConsultationInfo(conversationId, common);
    }

    /************************* 查看正在进行的会话详情 *************************/

    @AuthRoles({Consultant.ROLE, Supervisor.ROLE})
    @ApiOperation(value = "咨询师/督导获得在线会话列表")
    @GetMapping("onlineConversationsList")
    public Responses<List<OnlineConversation>> getOnlineConversationsList(HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getOnlineConversationsList(common);
    }

    @AuthRoles(Consultant.ROLE)
    @ApiOperation("咨询师获得在线会话的基本信息")
    @GetMapping("onlineConsultationInfo")
    public Responses<WebConversationInfoResponse> getOnlineConsultationInfo(@RequestParam String conversationId, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getOnlineConsultationInfo(conversationId, common);
    }

    @AuthRoles(Supervisor.ROLE)
    @ApiOperation("督导获得在线会话的基本信息")
    @GetMapping("onlineHelpInfo")
    public Responses<WebConversationInfoResponse> getOnlineHelpInfo(@RequestParam String conversationId, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return conversationService.getOnlineHelpInfo(conversationId, common);
    }


}
