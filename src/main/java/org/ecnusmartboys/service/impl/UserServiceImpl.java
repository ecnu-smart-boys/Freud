package org.ecnusmartboys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.mapstruct.UserInfoMapper;
import org.ecnusmartboys.model.dto.UserInfo;
import org.ecnusmartboys.model.entity.User;
import org.ecnusmartboys.model.entity.Visitor;
import org.ecnusmartboys.model.request.WxRegisterReq;
import org.ecnusmartboys.repository.StaffRepository;
import org.ecnusmartboys.repository.UserRepository;
import org.ecnusmartboys.repository.VisitorRepository;
import org.ecnusmartboys.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserRepository, User> implements UserService {

    private final VisitorRepository visitorRepository;

    private final StaffRepository staffRepository;

    private final UserInfoMapper userInfoMapper;

    @Override
    @Transactional
    public User saveVisitor(WxRegisterReq req) {
        User user = new User();
        BeanUtils.copyProperties(req, user);
        user.setRoles(Collections.singletonList(ROLE_VISITOR));
        getBaseMapper().insert(user);

        Visitor visitor = new Visitor();
        visitor.setId(user.getId());
        visitor.setEmergencyContact(req.getEmergencyContact());
        visitor.setEmergencyPhone(req.getEmergencyPhone());
        visitorRepository.insert(visitor);

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
        var userInfo = userInfoMapper.toDto(user);
        if (user.getRoles() != null) {
            // 假设访客与咨询师督导互斥
            if (user.getRoles().contains(ROLE_VISITOR)) {
                userInfo.setVisitor(visitorRepository.selectById(id));
            } else {
                userInfo.setStaff(staffRepository.selectById(id));
            }
        }

        return userInfo;
    }
}
