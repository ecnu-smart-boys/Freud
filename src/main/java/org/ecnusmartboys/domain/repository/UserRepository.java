package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.domain.model.PageResult;
import org.ecnusmartboys.domain.model.user.User;

import java.util.List;

public interface UserRepository {
    User retrieveById(String ID);

    User retrieveByOpenId(String openID);

    User retrieveByUsername(String username);

    User retrieveByPhone(String phone);

    PageResult<User> retrieveByRoleAndPage(String role, Long current, Long size, String name);

    void save(User user);

    void update(User user);

    List<String> retrieveIdsByArrangement(int dayOfWeek);

    List<User> retrieveByRole(String role, String name);

}
