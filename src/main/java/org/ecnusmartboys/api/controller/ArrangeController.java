package org.ecnusmartboys.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.request.command.AddArrangementRequest;
import org.ecnusmartboys.application.dto.request.command.RemoveArrangeRequest;
import org.ecnusmartboys.application.dto.response.Response;
import org.ecnusmartboys.application.service.ArrangeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.ecnusmartboys.infrastructure.service.UserService.*;

@Slf4j
@RestController
@RequestMapping("/arrangement")
@RequiredArgsConstructor
@Api(tags = "排班管理接口")
public class ArrangeController {

    private final ArrangeService arrangeService;

    @AuthRoles(ROLE_ADMIN)
    @ApiOperation("移除排班")
    @PostMapping("/remove")
    public Response<Object> remove(@RequestBody RemoveArrangeRequest req) {
        return arrangeService.remove(req);

        //arrangementService.remove(new QueryWrapper<Arrangement>().eq("user_id", id).eq("date", date));
    }

    @AuthRoles(ROLE_ADMIN)
    @ApiOperation("添加排班")
    @PostMapping("/add")
    public Response<Object> addConsultant(@RequestBody @Validated AddArrangementRequest req) {
        return arrangeService.addConsultant(req);
    }
}
