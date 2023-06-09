package org.ecnusmartboys.infrastructure.repositoryimpl;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.domain.model.PageResult;
import org.ecnusmartboys.domain.model.user.*;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.convertor.UserConvertor;
import org.ecnusmartboys.infrastructure.data.mysql.table.StaffInfoDO;
import org.ecnusmartboys.infrastructure.data.mysql.table.UserDO;
import org.ecnusmartboys.infrastructure.data.mysql.table.VisitorInfoDO;
import org.ecnusmartboys.infrastructure.mapper.StaffInfoMapper;
import org.ecnusmartboys.infrastructure.mapper.UserMapper;
import org.ecnusmartboys.infrastructure.mapper.VisitorInfoMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository, InitializingBean {
    private final UserMapper userMapper;
    private final StaffInfoMapper staffInfoMapper;
    private final VisitorInfoMapper visitorInfoMapper;
    private final UserConvertor userConvertor;

    @Override
    public User retrieveById(String ID) {
        var userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getId, ID));
        if (userDO == null) {
            return null;
        }
        return convert(userDO);
    }

    @Override
    public User retrieveByOpenId(String openID) {
        var userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getOpenID, openID));
        if (userDO == null) {
            return null;
        }
        return convert(userDO);
    }

    @Override
    public User retrieveByUsername(String username) {
        var userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername, username));
        if (userDO == null) {
            return null;
        }
        return convert(userDO);
    }

    @Override
    public User retrieveByPhone(String phone) {
        var userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getPhone, phone));
        if (userDO == null) {
            return null;
        }
        return convert(userDO);
    }

    @Override
    public PageResult<User> retrieveByRoleAndPage(String role, Long current, Long size, String name) {
        Page<UserDO> page = new Page<>(current, size);
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>().eq(UserDO::getRole, role);
        if (!name.equals("")) {
            wrapper.like(UserDO::getName, name);
        }
        userMapper.selectPage(page, wrapper);

        List<UserDO> userDOS = page.getRecords();
        List<User> users = new ArrayList<>();
        userDOS.forEach(v -> {
            users.add(convert(v));
        });
        return new PageResult<>(users, page.getTotal());
    }


    private User convert(UserDO userDO) {
        String role = userDO.getRole();
        switch (role) {
            case Visitor.ROLE:
                var visitorInfoDO = visitorInfoMapper.selectOne(new LambdaQueryWrapper<VisitorInfoDO>().eq(VisitorInfoDO::getVisitorId, userDO.getId()));
                return userConvertor.toVisitor(userDO, visitorInfoDO);
            case Admin.ROLE:
                return userConvertor.toAdmin(userDO);
            case Supervisor.ROLE:
            case Consultant.ROLE:
                var staffInfoDO = staffInfoMapper.selectOne(new LambdaQueryWrapper<StaffInfoDO>().eq(StaffInfoDO::getStaffId, userDO.getId()));
                return role.equals(Consultant.ROLE) ? userConvertor.toConsultant(userDO, staffInfoDO) : userConvertor.toSupervisor(userDO, staffInfoDO);
        }
        return userConvertor.toUser(userDO);
    }


    @Override
    @Transactional
    public void save(User user) {
        var userDO = userConvertor.toUserDO(user);
        userMapper.insert(userDO);
        if (user instanceof Visitor) {
            var visitorInfoDO = userConvertor.toVisitorInfoDO((Visitor) user);
            visitorInfoDO.setVisitorId(userDO.getId());
            visitorInfoMapper.insert(visitorInfoDO);
            user.setId(String.valueOf(visitorInfoDO.getVisitorId()));
        } else if (user instanceof Consultant) {
            var staffInfoDO = userConvertor.toStaffInfoDO((Consultant) user);
            staffInfoDO.setStaffId(userDO.getId());
            staffInfoDO.setMaxConversations(5);
            staffInfoMapper.insert(staffInfoDO);
            user.setId(String.valueOf(staffInfoDO.getStaffId()));
        } else if (user instanceof Supervisor) {
            var staffInfoDO = userConvertor.toStaffInfoDO((Supervisor) user);
            staffInfoDO.setStaffId(userDO.getId());
            staffInfoDO.setMaxConversations(5);
            staffInfoMapper.insert(staffInfoDO);
            user.setId(String.valueOf(staffInfoDO.getStaffId()));
        }
    }

    @Override
    @Transactional
    public void update(User user) {
        var userDO = userConvertor.toUserDO(user);
        userMapper.updateById(userDO);
        if (user instanceof Visitor) {
            var visitorInfoDO = userConvertor.toVisitorInfoDO((Visitor) user);
            visitorInfoDO.setVisitorId(userDO.getId());
            visitorInfoMapper.updateById(visitorInfoDO);
        } else if (user instanceof Consultant) {
            var staffInfoDO = userConvertor.toStaffInfoDO((Consultant) user);
            staffInfoDO.setStaffId(userDO.getId());
            staffInfoMapper.updateById(staffInfoDO);
        } else if (user instanceof Supervisor) {
            var staffInfoDO = userConvertor.toStaffInfoDO((Supervisor) user);
            staffInfoDO.setStaffId(userDO.getId());
            staffInfoMapper.updateById(staffInfoDO);
        }
    }

    @Override
    public List<String> retrieveIdsByArrangement(int dayOfWeek) {
        var wrapper = new QueryWrapper<StaffInfoDO>().eq(
                "arrangement & " + (1 << dayOfWeek), 1 << dayOfWeek);
        var staffList = staffInfoMapper.selectList(wrapper);
        List<String> results = new ArrayList<>();
        staffList.forEach(v -> {
            results.add(v.getStaffId().toString());
        });
        return results;
    }

    @Override
    public List<User> retrieveByRole(String role, String name) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>().eq(UserDO::getRole, role);
        if (!name.equals("")) {
            wrapper.like(UserDO::getName, name);
        }
        List<UserDO> userDOS = userMapper.selectList(wrapper);
        List<User> users = new ArrayList<>();
        userDOS.forEach(userDO -> {
            users.add(userConvertor.toUser(userDO));
        });
        return users;
    }


    @Override
    public void afterPropertiesSet() {
        // 创建超级管理员
        Long count = userMapper.selectCount(new LambdaQueryWrapper<UserDO>().eq(UserDO::getRole, Admin.ROLE));
        if (count == 0) {
            UserDO userDO = new UserDO();
            userDO.setName("弗洛伊德");
            userDO.setAvatar("https://ts1.cn.mm.bing.net/th/id/R-C.45b3a4f888e913e1ada56e2950bbd193?rik=ScD5TlSiXveZ9A&riu=http%3a%2f%2fwww.cuimianxinli.com%2fupload%2f2016-12%2f16122009305407.jpg&ehk=%2fzLL0q3fqB7F%2b8YM4OpdDSd33tZowsGpwk0VLco4p7g%3d&risl=&pid=ImgRaw&r=0");
            userDO.setAge(167);
            userDO.setGender(0);
            var rawPassword = "freud_admin" + System.currentTimeMillis();
            userDO.setPassword(MD5.create().digestHex(rawPassword).toUpperCase());
            userDO.setRole(Admin.ROLE);
            userMapper.insert(userDO);
            log.info("创建超级管理员成功，用户名：{}，密码：{}", userDO.getUsername(), rawPassword);
        }
    }
}
