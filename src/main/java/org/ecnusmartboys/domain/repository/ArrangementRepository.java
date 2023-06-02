package org.ecnusmartboys.domain.repository;


import org.ecnusmartboys.domain.model.arrangement.Arrangement;
import org.ecnusmartboys.infrastructure.data.mysql.intermidium.ArrangementInfo;

import java.time.Month;
import java.util.Date;
import java.util.List;

public interface ArrangementRepository {

    void save(String userId, Date date);

    List<Arrangement> retrieveByDate(Date date);

    void remove(String userId, Date date);

    List<ArrangementInfo> retrieveMonthArrangement(int year, int month);

    List<Integer> retrieveMonthArrangementByUserId(int year, int month, String userId);
}
