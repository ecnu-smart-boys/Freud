package org.ecnusmartboys.infrastructure.repositoryimpl;

import org.ecnusmartboys.domain.model.user.User;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.domain.repository.UserRepository;

public class UserRepositoryImpl implements UserRepository {
    @Override
    public User retrieveByOpenId(String openID, String role) {
        return null;
    }

    @Override
    public User retrieveByName(String name, String role) {
        return null;
    }

    @Override
    public void save(User user) {


        if (user instanceof Visitor) {
            Object o = (Object) user;

        }
    }
}
