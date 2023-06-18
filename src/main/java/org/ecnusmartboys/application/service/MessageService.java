package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.AllMessageRequest;
import org.ecnusmartboys.application.dto.request.command.SynchronizeMsgRequest;
import org.ecnusmartboys.application.dto.request.query.SingleMsgRequest;
import org.ecnusmartboys.application.dto.response.*;
import org.ecnusmartboys.infrastructure.data.im.IMCallbackParam;

import javax.servlet.http.HttpServletRequest;

public interface MessageService {

    /**
     * IM消息回调
     */
    Responses<?> callback(IMCallbackParam param, String body, HttpServletRequest request);

    /**
     * 生成签名
     */
    Responses<SigResponse> generateUserSig(Common common);

    /**
     * 督导查看自己的求助记录消息列表
     */
    Responses<AllMsgListResponse> getSupervisorOwnHelpMsg(AllMessageRequest req, Common common);

    /**
     * 督导查看自己绑定的咨询师的咨询记录消息列表
     */
    Responses<AllMsgListResponse> getBoundConsultantsMsg(AllMessageRequest req, Common common);

    /**
     * 咨询师查看自己的咨询记录消息列表
     */
    Responses<AllMsgListResponse> getConsultantOwnConsultationMsg(AllMessageRequest req, Common common);

    /**
     * 咨询师查看咨询记录消息列表
     */
    Responses<AllMsgListResponse> getAdminConsultationMsg(AllMessageRequest req, Common common);

    /**
     * 访客查看咨询记录消息列表
     */
    Responses<AllDetailsResponse> getVisitorConsultationMsg(String conversationId, Common common);

    /**
     * 督导同步咨询师和访客的聊天记录
     */
    Responses<MsgListResponse> synchronizeConsultationMsg(SynchronizeMsgRequest req, Common common);
}
