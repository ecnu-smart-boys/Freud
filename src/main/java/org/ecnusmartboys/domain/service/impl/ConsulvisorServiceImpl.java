package org.ecnusmartboys.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.domain.service.ConsulvisorService;
import org.ecnusmartboys.infrastructure.data.mysql.Consulvisor;
import org.ecnusmartboys.infrastructure.repository.ConsulvisorRepository;
import org.ecnusmartboys.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsulvisorServiceImpl
    extends ServiceImpl<ConsulvisorRepository, Consulvisor>
    implements ConsulvisorService {

    private final UserRepository userRepository;

    @Override
    public List<String> getSupervisors(Long consultantId) {
        List<String> names = new ArrayList<>();
        List<Consulvisor> consulvisorList = getBaseMapper().selectByConsultantId(consultantId);
        consulvisorList.forEach(v -> {
            names.add(userRepository.selectById(v.getSupervisorId()).getName());
        });
        return names;
    }

    @Override
    public List<String> getConsultants(Long supervisorId) {
        List<String> names = new ArrayList<>();
        List<Consulvisor> consulvisorList = getBaseMapper().selectBySupervisorId(supervisorId);
        consulvisorList.forEach(v -> {
            names.add(userRepository.selectById(v.getConsultantId()).getName());
        });
        return names;
    }
}
