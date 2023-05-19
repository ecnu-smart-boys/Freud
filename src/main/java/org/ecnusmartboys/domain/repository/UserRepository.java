package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.domain.model.user.User;

import java.util.List;

public interface UserRepository {
    User retrieveByOpenId(String openID, String role);
    User retrieveByName(String name, String role);
    List<User> retrieveByRole(String role);
    void save(User user);
}
