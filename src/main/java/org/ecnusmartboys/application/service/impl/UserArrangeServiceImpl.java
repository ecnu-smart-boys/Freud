package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.convertor.UserInfoConvertor;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.query.UserListReq;
import org.ecnusmartboys.application.dto.response.ConsultantsResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.dto.response.SupervisorsResponse;
import org.ecnusmartboys.application.dto.response.VisitorsResponse;
import org.ecnusmartboys.application.service.UserArrangeService;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserArrangeServiceImpl implements UserArrangeService {
    private final UserRepository userRepository;
    private final UserInfoConvertor userInfoConvertor;
    @Override
    public Responses<ConsultantsResponse> getConsultants(UserListReq req) {
        List<UserInfo> consultantInfoList = new ArrayList<>();
        var consultants = userRepository.retrieveByRole(Consultant.ROLE);
        consultants.forEach( v -> {
            var consultantInfo = userInfoConvertor.fromEntity(v);

            // TODO 累计咨询次数，咨询时间，平均评价
            consultantInfo.setConsultNum(0);
            consultantInfo.setAccumulatedTime(0L);
            // TODO 排班

            consultantInfoList.add(consultantInfo);
        });
        return Responses.ok(new ConsultantsResponse(consultantInfoList, (long) consultantInfoList.size()));
    }

    @Override
    public Responses<SupervisorsResponse> getSupervisors(UserListReq req) {
        List<SupervisorInfo> supervisorInfoList = new ArrayList<>();
        var supervisors = userService.getUsers(req, ROLE_SUPERVISOR);
        supervisors.forEach( v -> {
            SupervisorInfo supervisorInfo = new SupervisorInfo();
            BeanUtils.copyProperties(v, supervisorInfo);

            supervisorInfo.setConsultants(consulvisorService.getConsultants(supervisorInfo.getId()));
            supervisorInfo.setStaff(staffService.getById(supervisorInfo.getId()));
            // TODO 累计咨询次数，咨询时间
            supervisorInfo.setConsultNum(0);
            supervisorInfo.setAccumulatedTime(0L);
            // TODO 排班

            supervisorInfoList.add(supervisorInfo);
        });
        return Responses.ok(new SupervisorsResponse(supervisorInfoList, userService.getUserCount(ROLE_SUPERVISOR)));
    }

    @Override
    public Responses<VisitorsResponse> getVisitors(UserListReq req) {
        List<UserInfo> visitorInfoList = new ArrayList<>();
        var visitors = userService.getUsers(req, Visitor);
        visitors.forEach( v -> {
            VisitorInfo visitorInfo = new VisitorInfo();
            BeanUtils.copyProperties(v, visitorInfo);

            visitorInfo.setVisitor(visitorService.getById(visitorInfo.getId()));
            visitorInfoList.add(visitorInfo);
        });
        return Responses.ok(new VisitorsDTO(visitorInfoList, userService.getUserCount(ROLE_VISITOR)));
    }
}
