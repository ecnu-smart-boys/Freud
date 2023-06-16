package org.ecnusmartboys.infrastructure.repositoryimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.domain.model.arrangement.Arrangement;
import org.ecnusmartboys.domain.repository.ArrangementRepository;
import org.ecnusmartboys.infrastructure.convertor.ArrangementConvertor;
import org.ecnusmartboys.infrastructure.data.mysql.intermidium.ArrangementInfo;
import org.ecnusmartboys.infrastructure.data.mysql.table.ArrangementDO;
import org.ecnusmartboys.infrastructure.mapper.ArrangementMapper;
import org.springframework.stereotype.Repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ArrangementRepositoryImpl implements ArrangementRepository {

    private final ArrangementMapper arrangementMapper;

    private final ArrangementConvertor arrangementConvertor;

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void save(String userId, Date date) {
        ArrangementDO arrangementDO = new ArrangementDO(date, Long.valueOf(userId));
        arrangementMapper.insert(arrangementDO);
    }

    @Override
    public List<Arrangement> retrieveByDate(Date date) {
        var arrangementDOS = arrangementMapper.selectByDate(dateFormat.format(date));
        return arrangementConvertor.toArrangements(arrangementDOS);
    }

    @Override
    public void remove(String userId, Date date) {
        arrangementMapper.deleteByDateAndUserId(dateFormat.format(date), userId);
    }

    @Override
    public List<ArrangementInfo> retrieveMonthArrangement(int year, int month) {
        return arrangementMapper.selectInfoByYearAndMonth(year, month);
    }

    @Override
    public List<Integer> retrieveMonthArrangementByUserId(int year, int month, String userId) {
        return arrangementMapper.selectDayByMonthAndDateAndUserId(year, month, Long.valueOf(userId));
    }
}
