package org.ecnusmartboys.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.Extractor;
import org.ecnusmartboys.api.annotation.AnonymousAccess;
import org.ecnusmartboys.api.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.request.command.AllMessageRequest;
import org.ecnusmartboys.application.dto.request.query.SingleMsgRequest;
import org.ecnusmartboys.application.dto.response.AllMsgListResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.dto.response.MsgListResponse;
import org.ecnusmartboys.application.service.MessageService;
import org.ecnusmartboys.domain.model.user.Admin;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.infrastructure.data.im.IMCallbackParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/im")
@RequiredArgsConstructor
@Api(tags = "IM管理接口")
public class IMController {


    private final MessageService messageService;

    @PostMapping("/cb")
    @AnonymousAccess
    public Responses<?> callback(IMCallbackParam param,
        @RequestBody String body, HttpServletRequest request) {
        return messageService.callback(param, body, request);
    }

    @AuthRoles(Supervisor.ROLE)
    @ApiOperation(value = "督导查看自己的求助记录消息列表")
    @GetMapping("/details/supervisorOwnHelpMsg")
    public Responses<AllMsgListResponse> getSupervisorOwnHelpMsg(@Validated AllMessageRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return messageService.getSupervisorOwnHelpMsg(req, common);
    }

    @AuthRoles(Supervisor.ROLE)
    @ApiOperation(value = "督导查看自己绑定的咨询师的咨询记录消息列表")
    @GetMapping("/details/boundConsultantsMsg")
    public Responses<AllMsgListResponse> getBoundConsultantsMsg(@Validated AllMessageRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return messageService.getBoundConsultantsMsg(req, common);
    }

    @AuthRoles(Consultant.ROLE)
    @ApiOperation(value = "咨询师查看自己的咨询记录消息列表")
    @GetMapping("/details/consultantOwnConsultationMsg")
    public Responses<AllMsgListResponse> getConsultantOwnConsultationMsg(@Validated AllMessageRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return messageService.getConsultantOwnConsultationMsg(req, common);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation(value = "管理员查看咨询记录消息列表")
    @GetMapping("/details/adminConsultationMsg")
    public Responses<AllMsgListResponse> getAdminConsultationMsg(@Validated AllMessageRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return messageService.getAdminConsultationMsg(req, common);
    }

    @AuthRoles(Visitor.ROLE)
    @ApiOperation("访客查看咨询记录消息列表")
    @GetMapping("details/visitorConsultationList")
    public Responses<MsgListResponse> getVisitorConsultationMsg(@Validated SingleMsgRequest req, HttpServletRequest request) {
        var common = Extractor.extract(request);
        return messageService.getVisitorConsultationMsg(req, common);
    }

    /************************* 获取在线会话的消息 *************************/

//    @AuthRoles(Consultant.ROLE)
//    @ApiOperation("咨询师查看当前会话的求助消息记录(求助已结束，IM端已经将求助会话消除)")
//    @GetMapping("/helpMsg")
//    public Responses<MsgListResponse> getHelpMsg(@Validated SingleMsgRequest req, HttpServletRequest request) {
//        var common = Extractor.extract(request);
//        return messageService.getHelpMsg(req, common);
//    }



}


