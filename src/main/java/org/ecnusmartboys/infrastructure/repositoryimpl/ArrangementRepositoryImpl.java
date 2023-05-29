package org.ecnusmartboys.infrastructure.repositoryimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.domain.repository.ArrangementRepository;
import org.ecnusmartboys.infrastructure.data.mysql.ArrangementDO;
import org.ecnusmartboys.infrastructure.mapper.ArrangementMapper;
import org.springframework.stereotype.Repository;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ArrangementRepositoryImpl implements ArrangementRepository {

    private final ArrangementMapper arrangementMapper;

    @Override
    public void save(String userId, Date date) {
        ArrangementDO arrangementDO = new ArrangementDO(date, Long.valueOf(userId));
        arrangementMapper.insert(arrangementDO);
    }
}
