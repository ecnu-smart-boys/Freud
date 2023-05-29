package org.ecnusmartboys.domain.repository;

import org.ecnusmartboys.domain.model.user.Consulvisor;

import java.util.List;

public interface ConsulvisorRepository {
    void save(Consulvisor consulvisor);

    void removeAll(String consultantId);

    List<Consulvisor> retrieveByConId(String id);

    List<Consulvisor> retrieveBySupId(String id);
}
