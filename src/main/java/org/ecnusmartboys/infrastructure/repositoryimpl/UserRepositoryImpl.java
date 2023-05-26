package org.ecnusmartboys.infrastructure.repositoryimpl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.domain.model.user.*;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.convertor.UserConvertor;
import org.ecnusmartboys.infrastructure.data.mysql.StaffInfoDO;
import org.ecnusmartboys.infrastructure.data.mysql.UserDO;
import org.ecnusmartboys.infrastructure.data.mysql.VisitorInfoDO;
import org.ecnusmartboys.infrastructure.mapper.StaffInfoMapper;
import org.ecnusmartboys.infrastructure.mapper.UserMapper;
import org.ecnusmartboys.infrastructure.mapper.VisitorInfoMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

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
        var userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getOpenId, openID));
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
    private User convert(UserDO userDO){
        String role = userDO.getRole();
        switch (role) {
            case Visitor.ROLE:
                var visitorInfoDO = visitorInfoMapper.selectOne(new LambdaQueryWrapper<VisitorInfoDO>().eq(VisitorInfoDO::getUserId, userDO.getId()));
                return userConvertor.toVisitor(userDO, visitorInfoDO);
            case Admin.ROLE:
                return userConvertor.toAdmin(userDO);
            case Supervisor.ROLE:
            case Consultant.ROLE:
                var staffInfoDO = staffInfoMapper.selectOne(new LambdaQueryWrapper<StaffInfoDO>().eq(StaffInfoDO::getUserId, userDO.getId()));
                return role.equals(Consultant.ROLE) ? userConvertor.toConsultant(userDO, staffInfoDO) : userConvertor.toSupervisor(userDO, staffInfoDO);
        }
        return userConvertor.toUser(userDO);
    }



    @Override
    public List<User> retrieveByRole(String role) {
        return null;
    }

    @Override
    public void save(User user) {
        //TODO save or update
        var userDO = userConvertor.toUserDO(user);
        userMapper.updateById(userDO);
        if (user instanceof Visitor) {
            var visitorInfoDO = userConvertor.toVisitorInfoDO((Visitor) user);
            visitorInfoMapper.updateById(visitorInfoDO);
        } else if (user instanceof Consultant) {
            var staffInfo = userConvertor.toStaffInfoDO((Consultant) user);
            staffInfoMapper.updateById(staffInfo);
        }
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
            userDO.setPassword(BCrypt.hashpw(rawPassword));
            userDO.setRole(Admin.ROLE);
            userMapper.insert(userDO);
            log.info("创建超级管理员成功，用户名：{}，密码：{}", userDO.getUsername(), rawPassword);
        }
    }
}
