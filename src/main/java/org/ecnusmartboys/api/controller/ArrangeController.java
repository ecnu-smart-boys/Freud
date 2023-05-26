package org.ecnusmartboys.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.request.command.AddArrangementRequest;
import org.ecnusmartboys.application.dto.request.command.RemoveArrangeRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.ArrangeService;
import org.ecnusmartboys.domain.model.user.Admin;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/arrangement")
@RequiredArgsConstructor
@Api(tags = "排班管理接口")
public class ArrangeController {

    private final ArrangeService arrangeService;

    @AuthRoles(Admin.ROLE)
    @ApiOperation("移除排班")
    @PostMapping("/remove")
    public Responses<Object> remove(@RequestBody RemoveArrangeRequest req) {
        return arrangeService.remove(req);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("添加排班")
    @PostMapping("/add")
    public Responses<Object> addConsultant(@RequestBody @Validated AddArrangementRequest req) {
        return arrangeService.addConsultant(req);
    }
}
