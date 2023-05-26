package org.ecnusmartboys.infrastructure.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.BaseQuery;
import org.ecnusmartboys.infrastructure.data.mysql.UserDO;
import org.ecnusmartboys.infrastructure.mapper.UserMapper;
import org.ecnusmartboys.infrastructure.service.UserService;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.ecnusmartboys.application.convertor.UserInfoConvertor;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.infrastructure.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService, InitializingBean {

    private final VisitorMapper visitorMapper;

    private final StaffMapper staffMapper;

    private final org.ecnusmartboys.domain.mapper.ConsulvisorMapper consulvisorMapper;

    private final UserInfoConvertor userInfoConvertor;

    @Override
    @Transactional
    public User saveVisitor(WxRegisterRequest req) {
        User user = new User();
        BeanUtils.copyProperties(req, user);
        user.setRoles(Collections.singletonList(ROLE_VISITOR));
        getBaseMapper().insert(user);

        Visitor visitor = new Visitor();
        visitor.setId(user.getId());
        visitor.setEmergencyContact(req.getEmergencyContact());
        visitor.setEmergencyPhone(req.getEmergencyPhone());
        visitorMapper.insert(visitor);

        return user;
    }

    @Override
    public UserInfo getUserInfo(Long id) {
        if (id == null) {
            return null;
        }
        var user = getById(id);
        if (user == null) {
            return null;
        }
        var userInfo = userInfoConvertor.toDto(user);
        if (user.getRoles() != null) {
            // 假设访客与咨询师督导互斥
            if (user.getRoles().contains(ROLE_VISITOR)) {
                userInfo.setVisitor(visitorMapper.selectById(id));
            } else {
                userInfo.setStaff(staffMapper.selectById(id));
            }
        }

        return userInfo;
    }

    @Override
    public List<UserDO> getUsers(UserListReq req, String role) {
        BaseQuery<User> query = new BaseQuery<>();
        query.setSize(req.getSize());
        query.setCurrent(req.getCurrent());
        Page<User> page = query.toPage();
        QueryWrapper<User> wrapper = query.toQueryWrapper();

        if(!req.getName().equals("")) {
            wrapper.like("name", req.getName());
        }

        getBaseMapper().selectPage(page, wrapper.like("roles", role));
        return page.getRecords();
    }

    @Override
    public long getUserCount(String role) {
        return getBaseMapper().selectCount(new QueryWrapper<User>().like("roles", role));
    }

    @Override
    public void disable(Long userId, String role) {
        var wrapper = new QueryWrapper<User>().eq("id", userId).like("roles", role);
        User user = getOne(wrapper);
        if(user == null) {
            throw new BadRequestException("该用户不存在");
        }

        if(user.getDisabled()) {
            throw new BadRequestException("请勿重复禁用该用户");
        }

        user.setDisabled(true);
        updateById(user);
    }

    @Override
    public void enable(Long userId, String role) {
        var wrapper = new QueryWrapper<User>().eq("id", userId).like("roles", role);
        User user = getOne(wrapper);
        if(user == null) {
            throw new BadRequestException("该用户不存在");
        }

        if(!user.getDisabled()) {
            throw new BadRequestException("请勿重复启用该用户");
        }

        user.setDisabled(false);
        updateById(user);
    }

    @Override
    @Transactional
    public void saveSupervisor(AddSupervisorRequest req) {
        User user = new User();
        BeanUtils.copyProperties(req, user);
        user.setRoles(Collections.singletonList(ROLE_SUPERVISOR));
        getBaseMapper().insert(user);

        Staff staff = new Staff();
        staff.setId(user.getId());
        BeanUtils.copyProperties(req, staff);
        staffMapper.insert(staff);
    }

    @Override
    public void updateSupervisor(UpdateSupervisorRequest req) {
        User user = new User();
        BeanUtils.copyProperties(req, user);
        user.setId(req.getSupervisorId());
        user.setRoles(Collections.singletonList(ROLE_SUPERVISOR));
        getBaseMapper().updateById(user);

        Staff staff = new Staff();
        staff.setId(req.getSupervisorId());
        BeanUtils.copyProperties(req, staff);
        staffMapper.updateById(staff);
    }

    @Override
    @Transactional
    public void saveConsultant(AddConsultantRequest req) {
        User user = new User();
        BeanUtils.copyProperties(req, user);
        user.setRoles(Collections.singletonList(ROLE_SUPERVISOR));
        getBaseMapper().insert(user);

        Staff staff = new Staff();
        staff.setId(user.getId());
        BeanUtils.copyProperties(req, staff);
        staffMapper.insert(staff);

        var ids = req.getSuperVisorIds();
        ids.forEach(id -> {
            Consulvisor consulvisor = new Consulvisor(user.getId(), id);
            consulvisorMapper.insert(consulvisor);
        });

    }

    @Override
    public User getByUsername(String username) {
        return getOne(new QueryWrapper<User>().eq("username", username));
    }

    @Override
    public User getByPhone(String phone) {
        return getOne(new QueryWrapper<User>().eq("phone", phone));
    }

    @Override
    public User getSingleUser(Long userId, String role) {
        return getOne(new QueryWrapper<User>().eq("id", userId).like("roles", role));
    }

    @Override
    public void afterPropertiesSet() {

    }
}
