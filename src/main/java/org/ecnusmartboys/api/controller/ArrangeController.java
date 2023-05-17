package org.ecnusmartboys.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.annotation.AuthRoles;
import org.ecnusmartboys.application.dto.request.command.AddArrangementReq;
import org.ecnusmartboys.application.dto.response.Response;
import org.ecnusmartboys.domain.service.ArrangementService;
import org.ecnusmartboys.domain.service.UserService;
import org.ecnusmartboys.infrastructure.data.mysql.Arrangement;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static org.ecnusmartboys.domain.service.UserService.*;

@Slf4j
@RestController
@RequestMapping("/arrangement")
@Api(tags = "排班管理接口")
public class ArrangeController {

    private final ArrangementService arrangementService;

    private final UserService userService;

    public ArrangeController(ArrangementService arrangementService, UserService userService) {
        this.arrangementService = arrangementService;
        this.userService = userService;
    }

    @AuthRoles(ROLE_ADMIN)
    @ApiOperation("移除排班")
    @DeleteMapping("/remove/{id}/{date}")
    public Response<Object> remove(@PathVariable Long id, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        arrangementService.remove(new QueryWrapper<Arrangement>().eq("user_id", id).eq("date", date));
        return Response.ok("成功移除排班");
    }

    @AuthRoles(ROLE_ADMIN)
    @ApiOperation("添加咨询师排班")
    @PostMapping("/add/consultant")
    public Response<Object> addConsultant(@RequestBody @Validated AddArrangementReq req) {
        return addArrangement(req, ROLE_CONSULTANT);
    }

    @AuthRoles(ROLE_ADMIN)
    @ApiOperation("添加督导排班")
    @PostMapping("/add/supervisor")
    public Response<Object> addSupervisor(@RequestBody @Validated AddArrangementReq req) {
        return addArrangement(req, ROLE_SUPERVISOR);
    }

    @NotNull
    private Response<Object> addArrangement(@Validated @RequestBody AddArrangementReq req, String roleConsultant) {
        if(userService.getSingleUser(req.getUserId(), roleConsultant) == null) {
            throw new BadRequestException("所要排班的督导不存在");
        }

        if(arrangementService.getArrangement(req) != null) {
            throw new BadRequestException("请勿重复排班");
        }

        Arrangement arrangement = new Arrangement(req.getDate(), req.getUserId());
        arrangementService.save(arrangement);
        return Response.ok("成功添加排班");
    }
}
