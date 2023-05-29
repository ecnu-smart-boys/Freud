package org.ecnusmartboys.domain.repository;

import java.util.Date;

public interface ArrangementRepository {

    void save(String userId, Date date);
}
