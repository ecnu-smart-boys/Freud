package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.convertor.UserInfoConvertor;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.query.UserListReq;
import org.ecnusmartboys.application.dto.response.ConsultantsResponse;
import org.ecnusmartboys.application.dto.response.Response;
import org.ecnusmartboys.application.service.UserArrangeService;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.ecnusmartboys.infrastructure.service.UserService.ROLE_CONSULTANT;

@RequiredArgsConstructor
@Service
public class UserArrangeServiceImpl implements UserArrangeService {
    private final UserRepository userRepository;
    private final UserInfoConvertor userInfoConvertor;
    @Override
    public Response<ConsultantsResponse> getConsultants(UserListReq req) {
        List<UserInfo> consultantInfoList = new ArrayList<>();
        var consultants = userRepository.retrieveByRole("consultant");
        consultants.forEach( v -> {
            var consultantInfo = userInfoConvertor.fromEntity(v);

            // TODO 累计咨询次数，咨询时间，平均评价
            consultantInfo.setConsultNum(0);
            consultantInfo.setAccumulatedTime(0L);
            // TODO 排班

            consultantInfoList.add(consultantInfo);
        });
        return Response.ok(new ConsultantsResponse(consultantInfoList, (long) consultantInfoList.size()));
    }
}
