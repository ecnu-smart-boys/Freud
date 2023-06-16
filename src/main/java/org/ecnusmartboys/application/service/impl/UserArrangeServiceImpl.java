package org.ecnusmartboys.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.doocs.im.ImClient;
import io.github.doocs.im.model.request.AccountImportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.convertor.*;
import org.ecnusmartboys.application.dto.ConsultantInfo;
import org.ecnusmartboys.application.dto.StaffBaseInfo;
import org.ecnusmartboys.application.dto.SupervisorInfo;
import org.ecnusmartboys.application.dto.VisitorInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.UserListReq;
import org.ecnusmartboys.application.dto.response.ConsultantsResponse;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.dto.response.SupervisorsResponse;
import org.ecnusmartboys.application.dto.response.VisitorsResponse;
import org.ecnusmartboys.application.dto.ws.Notify;
import org.ecnusmartboys.application.service.UserArrangeService;
import org.ecnusmartboys.domain.model.conversation.Conversation;
import org.ecnusmartboys.domain.model.user.*;
import org.ecnusmartboys.domain.repository.ConsulvisorRepository;
import org.ecnusmartboys.domain.repository.ConversationRepository;
import org.ecnusmartboys.domain.repository.OnlineUserRepository;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.ecnusmartboys.infrastructure.exception.InternalException;
import org.ecnusmartboys.infrastructure.ws.WebSocketServer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserArrangeServiceImpl implements UserArrangeService {
    private final UserRepository userRepository;
    private final ConsulvisorRepository consulvisorRepository;
    private final ConversationRepository conversationRepository;
    private final OnlineUserRepository onlineUserRepository;

    private final ConsultantInfoConvertor consultantInfoConvertor;
    private final SupervisorInfoConvertor supervisorInfoConvertor;
    private final VisitorInfoConvertor visitorInfoConvertor;
    private final AddSupReqConvertor addSupReqConvertor;
    private final UpdateSupReqConvertor updateSupReqConvertor;
    private final AddConReqConvertor addConReqConvertor;
    private final UpdateConReqConvertor updateConReqConvertor;

    private final ImClient adminClient;
    private final WebSocketServer webSocketServer;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Responses<ConsultantsResponse> getConsultants(UserListReq req) {
        List<ConsultantInfo> consultantInfoList = new ArrayList<>();
        var pageResult = userRepository.retrieveByRoleAndPage(Consultant.ROLE, req.getCurrent(), req.getSize(), req.getName());
        var consultants = pageResult.getData();
        consultants.forEach(v -> {
            var consultantInfo = consultantInfoConvertor.fromEntity((Consultant) v);

            // 累计咨询次数，咨询时间
            List<Conversation> conversations = conversationRepository.retrieveConsultationByToId(v.getId());
            int totalTime = 0;
            int totalScore = 0;
            for (Conversation conversation : conversations) {
                totalTime = (int) (totalTime + (conversation.getEndTime() - conversation.getStartTime()));
                totalScore += conversation.getFromUserComment().getScore();
            }
            consultantInfo.setTotalTime(totalTime);
            consultantInfo.setConsultTimes(conversations.size());

            // 平均评价
            if (conversations.size() == 0) {
                consultantInfo.setAvgComment(0);
            } else {
                consultantInfo.setAvgComment(totalScore / conversations.size());
            }

            // 获得绑定的督导
            consultantInfo.setSupervisorList(new ArrayList<>());
            var consulvisors = consulvisorRepository.retrieveByConId(consultantInfo.getId());
            consulvisors.forEach(consulvisor -> {
                var supervisor = userRepository.retrieveById(consulvisor.getSupervisorId());
                consultantInfo.getSupervisorList().add(new StaffBaseInfo(supervisor.getId(), supervisor.getName(), supervisor.getAvatar()));
            });

            consultantInfoList.add(consultantInfo);
        });
        return Responses.ok(new ConsultantsResponse(consultantInfoList, pageResult.getTotal()));
    }

    @Override
    public Responses<SupervisorsResponse> getSupervisors(UserListReq req) {
        List<SupervisorInfo> supervisorInfoList = new ArrayList<>();
        var pageResult = userRepository.retrieveByRoleAndPage(Supervisor.ROLE, req.getCurrent(), req.getSize(), req.getName());
        var supervisors = pageResult.getData();
        supervisors.forEach(v -> {
            var supervisorInfo = supervisorInfoConvertor.fromEntity((Supervisor) v);

            // 总咨询时长和咨询次数
            List<Conversation> conversations = conversationRepository.retrieveHelpByToId(v.getId());
            int totalTime = 0;
            for (Conversation conversation : conversations) {
                totalTime = (int) (totalTime + (conversation.getEndTime() - conversation.getStartTime()));
            }
            supervisorInfo.setTotalTime(totalTime);
            supervisorInfo.setConsultTimes(conversations.size());

            // 获得咨询师列表
            supervisorInfo.setConsultantList(new ArrayList<>());
            var consulvisors = consulvisorRepository.retrieveBySupId(supervisorInfo.getId());
            consulvisors.forEach(consulvisor -> {
                var consultant = userRepository.retrieveById(consulvisor.getConsultantId());
                supervisorInfo.getConsultantList().add(new StaffBaseInfo(consultant.getId(), consultant.getName(), consultant.getAvatar()));
            });

            supervisorInfoList.add(supervisorInfo);
        });
        return Responses.ok(new SupervisorsResponse(supervisorInfoList, pageResult.getTotal()));
    }

    @Override
    public Responses<VisitorsResponse> getVisitors(UserListReq req) {
        List<VisitorInfo> visitorInfoList = new ArrayList<>();
        var pageResult = userRepository.retrieveByRoleAndPage(Visitor.ROLE, req.getCurrent(), req.getSize(), req.getName());
        var visitors = pageResult.getData();
        visitors.forEach(v -> {
            var visitor = visitorInfoConvertor.fromEntity((Visitor) v);

            // 总咨询时长
            List<Conversation> conversations = conversationRepository.retrieveConsultationByFromId(v.getId());
            int totalTime = 0;
            for (Conversation conversation : conversations) {
                totalTime = (int) (totalTime + (conversation.getEndTime() - conversation.getStartTime()));
            }

            visitorInfoList.add(visitor);
        });
        return Responses.ok(new VisitorsResponse(visitorInfoList, pageResult.getTotal()));
    }

    @Override
    public Responses<Object> disable(DisableUserRequest request, Common common) {
        if (Objects.equals(request.getId(), common.getUserId())) {
            throw new BadRequestException("管理员不能禁用自己");
        }

        var user = userRepository.retrieveById(request.getId());
        if (user == null) {
            throw new InternalException("所要禁用的用户不存在");
        }

        if (user.isDisabled()) {
            throw new BadRequestException("请勿重复禁用该用户");
        }

        user.setDisabled(true);
        userRepository.update(user);

        // 将用户踢下线
        onlineUserRepository.logout(user.getId());
        webSocketServer.close(Long.valueOf(user.getId()));
        var kickRequest = io.github.doocs.im.model.request.KickRequest.builder().userId(user.getId()).build();
        try {
            adminClient.account.kick(kickRequest);
        } catch (IOException e) {
            log.error("IM踢下线失败, userId {}, {}", user.getId(), e.getMessage());
        }

        // 通知用户被禁用(如果在线的话)
        var notify = new Notify("endConsultation", null);
        try {
            webSocketServer.notifyUser(Long.valueOf(user.getId()), mapper.writeValueAsString(notify));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Responses.ok("禁用用户成功");
    }

    @Override
    public Responses<Object> enable(EnableUserRequest request, Common common) {
        if (Objects.equals(request.getId(), common.getUserId())) {
            throw new BadRequestException("管理员不能启用自己");
        }

        var user = userRepository.retrieveById(request.getId());
        if (user == null) {
            throw new InternalException("所要启用的用户不存在");
        }

        if (!user.isDisabled()) {
            throw new BadRequestException("请勿重复启用该用户");
        }

        user.setDisabled(false);
        userRepository.update(user);

        return Responses.ok("启用用户成功");
    }

    @Override
    public Responses<Object> saveSupervisor(AddSupervisorRequest req) {
        if (userRepository.retrieveByUsername(req.getUsername()) != null) {
            throw new BadRequestException("该用户名已被注册");
        }
        if (userRepository.retrieveByPhone(req.getPhone()) != null) {
            throw new BadRequestException("该手机号已被注册");
        }

        var supervisor = addSupReqConvertor.toEntity(req);
        supervisor.setRole(Supervisor.ROLE);
        userRepository.save(supervisor);
        importImUser(supervisor);
        return Responses.ok("成功添加督导");
    }

    @Override
    public Responses<Object> updateSupervisor(UpdateSupervisorRequest req) {
        var user = userRepository.retrieveById(req.getId());
        if (!(user instanceof Supervisor)) {
            throw new BadRequestException("所要更新的督导不存在");
        }

        var supervisor = updateSupReqConvertor.toEntity(req);
        userRepository.update(supervisor);
        importImUser(supervisor);
        return Responses.ok("成功更新督导");
    }

    private void importImUser(User user) {
        var req = AccountImportRequest.builder()
                .userId(user.getId())
                .faceUrl(user.getAvatar())
                .nick(user.getName())
                .build();
        try {
            var resp = adminClient.account.accountImport(req);
        } catch (IOException e) {
            log.error("IM导入用户失败", e);
        }
    }

    @Transactional
    @Override
    public Responses<Object> saveConsultant(AddConsultantRequest req) {
        if (userRepository.retrieveByUsername(req.getUsername()) != null) {
            throw new BadRequestException("该用户名已被注册");
        }
        if (userRepository.retrieveByPhone(req.getPhone()) != null) {
            throw new BadRequestException("该手机号已被注册");
        }

        for (String supervisorId : req.getSupervisorIds()) {
            var user = userRepository.retrieveById(supervisorId);
            if (!(user instanceof Supervisor)) {
                throw new BadRequestException("所要绑定的督导不存在");
            }

            var consulvisors = consulvisorRepository.retrieveBySupId(user.getId());
            if (consulvisors.size() >= 10) {
                throw new RuntimeException("督导" + user.getName() + "绑定的咨询师已达上线");
            }
        }

        var user = addConReqConvertor.toEntity(req);
        user.setRole(Consultant.ROLE);
        userRepository.save(user);
        var consultant = userRepository.retrieveByUsername(req.getUsername());
        for (String supervisorId : req.getSupervisorIds()) {
            consulvisorRepository.save(new Consulvisor(consultant.getId(), supervisorId));
        }
        return Responses.ok("成功添加咨询师");
    }

    @Transactional
    @Override
    public Responses<Object> updateConsultant(UpdateConsultantRequest req) {
        var user = userRepository.retrieveById(req.getId());
        if (!(user instanceof Consultant)) {
            throw new BadRequestException("所要修改的咨询师不存在");
        }

        // 咨询师正在通话中
        if (onlineUserRepository.isStaffInConversation(req.getId())) {
            throw new BadRequestException("咨询师正在通话中，无法修改");
        }

        for (String supervisorId : req.getSupervisorIds()) {
            user = userRepository.retrieveById(supervisorId);
            if (!(user instanceof Supervisor)) {
                throw new BadRequestException("所要绑定的督导不存在");
            }

            var consulvisors = consulvisorRepository.retrieveBySupId(user.getId());
            if (consulvisors.size() >= 10) {
                throw new RuntimeException("督导" + user.getName() + "绑定的咨询师已达上线");
            }
        }

        user = updateConReqConvertor.toEntity(req);
        userRepository.update(user);

        consulvisorRepository.removeAll(req.getId()); // 先删除所有绑定督导
        for (String supervisorId : req.getSupervisorIds()) {
            consulvisorRepository.save(new Consulvisor(user.getId(), supervisorId));
        }
        return Responses.ok("成功修改咨询师");
    }

    @Override
    public Responses<Object> updateArrangement(UpdateArrangementRequest req) {
        var user = userRepository.retrieveById(req.getId());
        if ((!(user instanceof Consultant) && !(user instanceof Supervisor))) {
            throw new BadRequestException("所要排班的用户不存在");
        }

        if (user instanceof Consultant) {
            ((Consultant) user).setArrangement(req.getArrangement());
        } else {
            ((Supervisor) user).setArrangement(req.getArrangement());
        }
        userRepository.update(user);

        return Responses.ok("成功修改排班");
    }

    @Override
    public Responses<List<StaffBaseInfo>> getAvailableSupervisors() {
        List<StaffBaseInfo> result = new ArrayList<>();
        List<String> notAvailableSupIds = consulvisorRepository.retrieveNotAvailableSupIds();
        Set<String> ids = new HashSet<>(notAvailableSupIds);
        ;

        List<User> supervisors = userRepository.retrieveByRole(Supervisor.ROLE, "");
        supervisors.forEach(supervisor -> {
            if (!ids.contains(supervisor.getId())) {
                result.add(new StaffBaseInfo(supervisor.getId(), supervisor.getName(), supervisor.getAvatar()));
            }
        });
        return Responses.ok(result);
    }
}
