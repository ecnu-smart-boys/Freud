package org.ecnusmartboys.api.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.request.command.StartConsultRequest;
import org.ecnusmartboys.application.dto.response.Response;
import org.ecnusmartboys.application.dto.response.StartConsultResponse;
import org.ecnusmartboys.infrastructure.utils.SecurityUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.ecnusmartboys.infrastructure.service.UserService.ROLE_VISITOR;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/conversation")
@Api(tags = "会话接口")
public class ConversationController {

    @AuthRoles(ROLE_VISITOR)
    @PostMapping("/consult")
    @ApiOperation("发起咨询会话")
    public Response<StartConsultResponse> startConversation(@RequestBody @Validated StartConsultRequest req){
        var userId = SecurityUtil.getCurrentUserId();

        return Response.ok();
    }
}
