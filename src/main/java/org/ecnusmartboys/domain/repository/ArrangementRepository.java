package org.ecnusmartboys.domain.repository;


import org.ecnusmartboys.domain.model.arrangement.Arrangement;
import org.ecnusmartboys.domain.model.arrangement.ArrangementInfo;

import java.util.Date;
import java.util.List;

public interface ArrangementRepository {

    void save(String userId, Date date);

    List<Arrangement> retrieveByDate(Date date);

    void remove(String userId, Date date);

    List<ArrangementInfo> retrieveMonthArrangement(Integer year, Integer month);
}
