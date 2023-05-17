package user;

import org.ecnusmartboys.FreudApp;
import org.ecnusmartboys.application.dto.SupervisorInfo;
import org.ecnusmartboys.application.dto.VisitorInfo;
import org.ecnusmartboys.application.dto.request.command.AddConsultantReq;
import org.ecnusmartboys.application.dto.request.command.AddSupervisorReq;
import org.ecnusmartboys.application.dto.request.command.UpdateSupervisorReq;
import org.ecnusmartboys.application.dto.request.command.UserListReq;
import org.ecnusmartboys.infrastructure.service.UserService;
import org.ecnusmartboys.infrastructure.service.VisitorService;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.ecnusmartboys.infrastructure.service.UserService.*;

@SpringBootTest(classes = FreudApp.class)
@ActiveProfiles({"local", "test"})
public class UserArrangeTest {

    @Resource
    UserService userService;

    @Resource
    ConsulvisorService consulvisorService;

    @Resource
    StaffService staffService;

    @Resource
    VisitorService visitorService;

    @Test
    public void testConsultants() {
//        List<ConsultantInfo> consultantInfoList = new ArrayList<>();
//        var consultants = userService.getUsers(new UserListReq(), UserService.ROLE_CONSULTANT);
//        consultants.forEach( v -> {
//            ConsultantInfo consultantInfo = new ConsultantInfo();
//            BeanUtils.copyProperties(v, consultantInfo);
//
//            consultantInfo.setSupervisor(consulvisorService.getSupervisor(consultantInfo.getId()));
//            consultantInfo.setStaff(staffService.getById(consultantInfo.getId()));
//            // TODO 累计咨询次数，咨询时间
//            consultantInfo.setConsultNum(0);
//            consultantInfo.setAccumulatedTime(0L);
//            System.out.println(consultantInfo);
//            consultantInfoList.add(consultantInfo);
//        });
//
//        System.out.println(userService.getUserCount(UserService.ROLE_CONSULTANT));
    }

    @Test
    public void testSupervisors() {
        var supervisors = userService.getUsers(new UserListReq(), ROLE_SUPERVISOR);
        supervisors.forEach(v -> {
            SupervisorInfo supervisorInfo = new SupervisorInfo();
            BeanUtils.copyProperties(v, supervisorInfo);

            supervisorInfo.setConsultants(consulvisorService.getConsultants(supervisorInfo.getId()));
            supervisorInfo.setStaff(staffService.getById(supervisorInfo.getId()));
            // TODO 累计咨询次数，咨询时间
            supervisorInfo.setConsultNum(0);
            supervisorInfo.setAccumulatedTime(0L);
            // TODO 排班

            System.out.println(supervisorInfo);
        });

        System.out.println(userService.getUserCount(ROLE_SUPERVISOR));
    }

    @Test
    public void testVisitors() {
        var visitors = userService.getUsers(new UserListReq(), ROLE_VISITOR);
        visitors.forEach( v -> {
            VisitorInfo visitorInfo = new VisitorInfo();
            BeanUtils.copyProperties(v, visitorInfo);

            visitorInfo.setVisitor(visitorService.getById(visitorInfo.getId()));
            System.out.println(visitorInfo);
        });

        System.out.println(userService.getUserCount(ROLE_SUPERVISOR));
    }


    @Test
    public void enableTest() {
        userService.enable(6L, ROLE_CONSULTANT);
    }

    @Test
    public void addSupervisorTest() {
        AddSupervisorReq req = new AddSupervisorReq();
        req.setUsername("consult_2");
        req.setName("咨询师测试");
        req.setPassword("password");
        req.setGender(1);
        req.setAge(20);
        req.setPhone("13611111112");
        req.setEmail("test@qq.com");
        req.setIdNumber("31011111111222222X");
        req.setDepartment("华东师范大学");
        req.setTitle("教授");
        req.setQualification("一级");
        req.setQualificationCode("55");
        if(userService.getByUsername(req.getUsername()) != null) {
            throw new BadRequestException("该用户名已存在");
        }
        userService.saveSupervisor(req);

    }

    @Test
    public void updateSupervisorTest() {
        UpdateSupervisorReq req = new UpdateSupervisorReq();
        req.setSupervisorId(13L);
        req.setName("督导修改测试");
        req.setGender(1);
        req.setAge(20);
        req.setEmail("test@qq.com");
        req.setIdNumber("31011111111222222X");
        req.setDepartment("华东师范大学");
        req.setTitle("讲席教授");
        req.setQualification("二级");
        req.setQualificationCode("55");

        userService.updateSupervisor(req);
    }

    @Test
    public void addConsultantTest() {
        AddConsultantReq req = new AddConsultantReq();
        req.setUsername("consult_3");
        req.setName("咨询师测试");
        req.setPassword("password");
        req.setGender(1);
        req.setAge(20);
        req.setPhone("13611111113");
        req.setEmail("test@qq.com");
        req.setIdNumber("31011111111222222X");
        req.setDepartment("华东师范大学");
        req.setTitle("讲席教授");
        List<Long> ids = new ArrayList<>();
        ids.add(11L);
        ids.add(13L);
        req.setSuperVisorIds(ids);
        if(userService.getByUsername(req.getUsername()) != null) {
            throw new BadRequestException("该用户名已存在");
        }

        userService.saveConsultant(req);
    }

}
