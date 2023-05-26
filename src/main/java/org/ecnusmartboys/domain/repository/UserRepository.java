package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.domain.model.user.User;

import java.util.List;

public interface UserRepository {
    User retrieveById(String ID);
    User retrieveByOpenId(String openID);
    User retrieveByName(String name);
    List<User> retrieveByRole(String role);
    void save(User user);
}
