package org.ecnusmartboys.infrastructure.repositoryimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.domain.model.user.Consulvisor;
import org.ecnusmartboys.domain.repository.ConsulvisorRepository;
import org.ecnusmartboys.infrastructure.convertor.ConsulvisorConvertor;
import org.ecnusmartboys.infrastructure.data.mysql.ConsulvisorDO;
import org.ecnusmartboys.infrastructure.mapper.ConsulvisorMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ConsulvisorRepositoryImpl implements ConsulvisorRepository {

    private final ConsulvisorMapper consulvisorMapper;

    private final ConsulvisorConvertor convertor;

    @Override
    public void save(Consulvisor consulvisor) {
        var consulvisorDO = convertor.toConsulvisor(consulvisor);
        consulvisorMapper.insert(consulvisorDO);
    }

    @Override
    public void removeAll(String consultantId) {
        consulvisorMapper.deleteByConsultantId(Long.valueOf(consultantId));
    }

    @Override
    public List<Consulvisor> retrieveByConId(String id) {
        var consulvisorDOs = consulvisorMapper.selectByConsultantId(Long.valueOf(id));
        return convertor.toConsulvisors(consulvisorDOs);
    }

    @Override
    public List<Consulvisor> retrieveBySupId(String id) {
        var consulvisorDOs = consulvisorMapper.selectBySupervisorId(Long.valueOf(id));
        return convertor.toConsulvisors(consulvisorDOs);
    }
}
