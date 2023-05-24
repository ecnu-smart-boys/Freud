package org.ecnusmartboys.infrastructure.repositoryimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.domain.model.user.User;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.convertor.UserConvertor;
import org.ecnusmartboys.infrastructure.data.mysql.RoleDO;
import org.ecnusmartboys.infrastructure.data.mysql.UserDO;
import org.ecnusmartboys.infrastructure.data.mysql.VisitorInfoDO;
import org.ecnusmartboys.infrastructure.mapper.RoleMapper;
import org.ecnusmartboys.infrastructure.mapper.StaffInfoMapper;
import org.ecnusmartboys.infrastructure.mapper.UserMapper;
import org.ecnusmartboys.infrastructure.mapper.VisitorInfoMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final StaffInfoMapper staffInfoMapper;
    private final VisitorInfoMapper visitorInfoMapper;
    private final UserConvertor userConvertor;
    @Override
    public User retrieveByOpenId(String openID, String role) {
        var userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getOpenId, openID));
        if (userDO == null) {
            return null;
        }
        var roleDO = getRole(userDO.getId(), role);
        if (roleDO == null) {
            return null;
        }
        return userConvertor.toUser(userDO, roleDO);
    }

    @Override
    public User retrieveByName(String name, String role) {
        var userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getName, name));
        if (userDO == null) {
            return null;
        }
        var roleDO = getRole(userDO.getId(), role);
        if (roleDO == null) {
            return null;
        }

        return userConvertor.toUser(userDO, roleDO);
    }

    private RoleDO getRole(Long id, String role) {
        return roleMapper.selectOne(new LambdaQueryWrapper<RoleDO>()
                .eq(RoleDO::getUserID, id)
                .eq(RoleDO::getUserID, role)
        );
    }

    @Override
    public List<User> retrieveByRole(String role) {
        return null;
    }

    @Override
    public void save(User user) {


        if (user instanceof Visitor) {
            Object o = (Object) user;

        }
    }
}
