package org.ecnusmartboys.adaptor.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.adaptor.Extractor;
import org.ecnusmartboys.adaptor.annotation.AuthRoles;
import org.ecnusmartboys.adaptor.annotation.Timestamp;
import org.ecnusmartboys.application.dto.DayArrangeInfo;
import org.ecnusmartboys.application.dto.StaffBaseInfo;
import org.ecnusmartboys.application.dto.request.command.AddArrangementRequest;
import org.ecnusmartboys.application.dto.request.command.RemoveArrangeRequest;
import org.ecnusmartboys.application.dto.request.query.GetMonthArrangementRequest;
import org.ecnusmartboys.application.dto.request.query.NoArrangedRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.ArrangeService;
import org.ecnusmartboys.domain.model.user.Admin;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/arrangement")
@RequiredArgsConstructor
@Api(tags = "排班管理接口")
public class ArrangeController {

    private final ArrangeService arrangeService;

    @AuthRoles(Admin.ROLE)
    @ApiOperation("移除排班")
    @PutMapping("/removeArrangement")
    public Responses<Object> remove(@RequestBody @Validated RemoveArrangeRequest req) {
        return arrangeService.remove(req);
    }

    @AuthRoles({Admin.ROLE})
    @ApiOperation("添加排班")
    @PostMapping("/addArrangement")
    public Responses<Object> addArrangement(@RequestBody @Validated AddArrangementRequest req) {
        return arrangeService.addArrangement(req);
    }

    @AuthRoles({Admin.ROLE})
    @ApiOperation("获得某日咨询师排班列表")
    @GetMapping("/consultants")
    public Responses<List<StaffBaseInfo>> getConsultants(@RequestParam @Timestamp Long timestamp) {
        return arrangeService.getConsultants(timestamp);
    }

    @AuthRoles({Admin.ROLE})
    @ApiOperation("获得某日督导排班列表")
    @GetMapping("/supervisors")
    public Responses<List<StaffBaseInfo>> getSupervisors(@RequestParam @Timestamp Long timestamp) {
        return arrangeService.getSupervisors(timestamp);
    }

    @AuthRoles({Admin.ROLE})
    @ApiOperation("获得单月总排班数据")
    @GetMapping("/monthArrangement")
    public Responses<List<DayArrangeInfo>> getMonthArrangement(@Validated GetMonthArrangementRequest req) {
        return arrangeService.getMonthArrangement(req);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("查询某日尚未排班的咨询师列表")
    @GetMapping("/notArrangedConsultants")
    public Responses<List<StaffBaseInfo>> getNoArrangedConsultants(@Validated NoArrangedRequest req) {
        return arrangeService.getNotArranged(req, Consultant.ROLE);
    }

    @AuthRoles(Admin.ROLE)
    @ApiOperation("查询某日尚未排班的督导列表")
    @GetMapping("/notArrangedSupervisors")
    public Responses<List<StaffBaseInfo>> getNoArrangedSupervisors(@Validated NoArrangedRequest req) {
        return arrangeService.getNotArranged(req, Supervisor.ROLE);
    }

    @AuthRoles({Consultant.ROLE, Supervisor.ROLE})
    @ApiOperation("查询个人月排班")
    @GetMapping("/personalArrangement")
    public Responses<List<Integer>> getPersonalArrangement(HttpServletRequest request) {
        var common = Extractor.extract(request);
        return arrangeService.getPersonalMonthArrangement(common);
    }

}
