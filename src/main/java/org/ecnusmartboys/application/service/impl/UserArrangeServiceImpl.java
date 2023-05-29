package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.convertor.*;
import org.ecnusmartboys.application.dto.*;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.UserListReq;
import org.ecnusmartboys.application.dto.response.ConsultantsResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.dto.response.SupervisorsResponse;
import org.ecnusmartboys.application.dto.response.VisitorsResponse;
import org.ecnusmartboys.application.service.UserArrangeService;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Consulvisor;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.domain.repository.ConsulvisorRepository;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.ecnusmartboys.infrastructure.exception.InternalException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserArrangeServiceImpl implements UserArrangeService {
    private final UserRepository userRepository;
    private final ConsulvisorRepository consulvisorRepository;

    private final ConsultantInfoConvertor consultantInfoConvertor;
    private final SupervisorInfoConvertor supervisorInfoConvertor;
    private final VisitorInfoConvertor visitorInfoConvertor;
    private final AddSupReqConvertor addSupReqConvertor;
    private final UpdateSupReqConvertor updateSupReqConvertor;
    private final AddConReqConvertor addConReqConvertor;
    private final UpdateConReqConvertor updateConReqConvertor;

    @Override
    public Responses<ConsultantsResponse> getConsultants(UserListReq req) {
        List<ConsultantInfo> consultantInfoList = new ArrayList<>();
        var pageResult = userRepository.retrieveByRoleAndPage(Consultant.ROLE, req.getCurrent(), req.getSize(), req.getName());
        var consultants = pageResult.getData();
        consultants.forEach( v -> {
            var consultantInfo = consultantInfoConvertor.fromEntity((Consultant) v);

            // TODO 累计咨询次数，咨询时间，平均评价
            consultantInfo.setConsultTimes(0);
            consultantInfo.setTotalTime(0);
            consultantInfo.setAvgComment(5);

            // 获得绑定的督导
            consultantInfo.setSupervisorNameList(new ArrayList<>());
            var consulvisors = consulvisorRepository.retrieveByConId(consultantInfo.getId());
            consulvisors.forEach(consulvisor -> {
                var supervisor = userRepository.retrieveById(consulvisor.getSupervisorId());
                consultantInfo.getSupervisorNameList().add(new SupervisorName(supervisor.getId(), supervisor.getName()));
            });

            consultantInfoList.add(consultantInfo);
        });
        return Responses.ok(new ConsultantsResponse(consultantInfoList, pageResult.getTotal())); // TODO
    }

    @Override
    public Responses<SupervisorsResponse> getSupervisors(UserListReq req) {
        List<SupervisorInfo> supervisorInfoList = new ArrayList<>();
        var pageResult = userRepository.retrieveByRoleAndPage(Supervisor.ROLE, req.getCurrent(), req.getSize(), req.getName());
        var supervisors = pageResult.getData();
        supervisors.forEach( v -> {
            var supervisorInfo = supervisorInfoConvertor.fromEntity( (Supervisor) v);

            // TODO 累计咨询次数，咨询时间
            supervisorInfo.setTotalTime(0);
            supervisorInfo.setConsultTimes(0);

            // 获得咨询师列表
            supervisorInfo.setConsultantNameList(new ArrayList<>());
            var consulvisors = consulvisorRepository.retrieveBySupId(supervisorInfo.getId());
            consulvisors.forEach(consulvisor -> {
                var consultant = userRepository.retrieveById(consulvisor.getConsultantId());
                supervisorInfo.getConsultantNameList().add(new ConsultantName(consultant.getId(), consultant.getName()));
            });

            supervisorInfoList.add(supervisorInfo);
        });
        return Responses.ok(new SupervisorsResponse(supervisorInfoList, pageResult.getTotal())); // TODO
    }

    @Override
    public Responses<VisitorsResponse> getVisitors(UserListReq req) {
        List<VisitorInfo> visitorInfoList = new ArrayList<>();
        var pageResult = userRepository.retrieveByRoleAndPage(Visitor.ROLE, req.getCurrent(), req.getSize(), req.getName());
        var visitors = pageResult.getData();
        visitors.forEach( v -> {
            visitorInfoList.add(visitorInfoConvertor.fromEntity((Visitor) v));
        });
        return Responses.ok(new VisitorsResponse(visitorInfoList, pageResult.getTotal()));
    }

    @Override
    public Responses<Object> disable(DisableUserRequest request, Common common) {
        if(Objects.equals(request.getId(), common.getUserId())) {
            throw new BadRequestException("管理员不能禁用自己");
        }

        var user = userRepository.retrieveById(request.getId());
        if(user == null) {
            throw new InternalException("所要禁用的用户不存在");
        }

        if(user.isDisabled()) {
            throw new BadRequestException("请勿重复禁用该用户");
        }

        user.setDisabled(true);
        userRepository.save(user);

        return Responses.ok("禁用用户成功");
    }

    @Override
    public Responses<Object> enable(EnableUserRequest request, Common common) {
        if(Objects.equals(request.getId(), common.getUserId())) {
            throw new BadRequestException("管理员不能启用自己");
        }

        var user = userRepository.retrieveById(request.getId());
        if(user == null) {
            throw new InternalException("所要启用的用户不存在");
        }

        if(!user.isDisabled()) {
            throw new BadRequestException("请勿重复启用该用户");
        }

        user.setDisabled(false);
        userRepository.save(user);

        return Responses.ok("启用用户成功");
    }

    @Override
    public Responses<Object> saveSupervisor(AddSupervisorRequest req) {
        if(userRepository.retrieveByUsername(req.getUsername()) != null) {
            throw new BadRequestException("该用户名已被注册");
        }
        if(userRepository.retrieveByPhone(req.getPhone()) != null) {
            throw new BadRequestException("该手机号已被注册");
        }

        var supervisor = addSupReqConvertor.toEntity(req);
        supervisor.setRole(Supervisor.ROLE);
        userRepository.save(supervisor);
        return Responses.ok("成功添加督导");
    }

    @Override
    public Responses<Object> updateSupervisor(UpdateSupervisorRequest req) {
        var user = userRepository.retrieveById(req.getId());
        if(!(user instanceof Supervisor)) {
            throw new BadRequestException("所要更新的督导不存在");
        }

        var supervisor =  updateSupReqConvertor.toEntity(req);
        userRepository.update(supervisor);
        return Responses.ok("成功更新督导");
    }

    @Transactional
    @Override
    public Responses<Object> saveConsultant(AddConsultantRequest req) {
        if(userRepository.retrieveByUsername(req.getUsername()) != null) {
            throw new BadRequestException("该用户名已被注册");
        }
        if(userRepository.retrieveByPhone(req.getPhone()) != null) {
            throw new BadRequestException("该手机号已被注册");
        }

        for(String supervisorId : req.getSupervisorIds()) {
            var user = userRepository.retrieveById(supervisorId);
            if(!(user instanceof Supervisor)) {
                throw new BadRequestException("所要绑定的督导不存在");
            }
        }

        var user = addConReqConvertor.toEntity(req);
        user.setRole(Consultant.ROLE);
        userRepository.save(user);
        var consultant = userRepository.retrieveByUsername(req.getUsername());
        for(String supervisorId : req.getSupervisorIds()) {
            consulvisorRepository.save(new Consulvisor(consultant.getId(), supervisorId));
        }
        return Responses.ok("成功添加咨询师");
    }

    @Transactional
    @Override
    public Responses<Object> updateConsultant(UpdateConsultantRequest req) {
        var user = userRepository.retrieveById(req.getId());
        if(!(user instanceof Consultant)) {
            throw new BadRequestException("所要修改的咨询师不存在");
        }

        for(String supervisorId : req.getSupervisorIds()) {
            user = userRepository.retrieveById(supervisorId);
            if(!(user instanceof Supervisor)) {
                throw new BadRequestException("所要绑定的督导不存在");
            }
        }

        user = updateConReqConvertor.toEntity(req);
        userRepository.update(user);

        consulvisorRepository.removeAll(req.getId()); // 先删除所有绑定督导
        for(String supervisorId : req.getSupervisorIds()) {
            consulvisorRepository.save(new Consulvisor(user.getId(), supervisorId));
        }
        return Responses.ok("成功修改咨询师");
    }
}
