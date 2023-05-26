package org.ecnusmartboys.api.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.Extractor;
import org.ecnusmartboys.api.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.request.command.StartConsultRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.dto.response.StartConsultResponse;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/conversation")
@Api(tags = "会话接口")
public class ConversationController {

    @AuthRoles(Visitor.ROLE)
    @PostMapping("/consult")
    @ApiOperation("发起咨询会话")
    public Responses<StartConsultResponse> startConversation(@RequestBody @Validated StartConsultRequest req, HttpServletRequest request){
        var common = Extractor.extract(request);
        return Responses.ok();
    }
}
