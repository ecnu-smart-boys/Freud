package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.domain.entity.User;
import org.ecnusmartboys.domain.entity.Visitor;

public interface VisitorRepository extends UserRepository {
    @Override
    Visitor retrieveByOpenId(String openID);
    @Override
    Visitor retrieveByName(String name);
    void save(Visitor visitor);
}
