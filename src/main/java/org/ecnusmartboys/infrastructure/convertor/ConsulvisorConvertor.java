package org.ecnusmartboys.infrastructure.convertor;

import org.ecnusmartboys.domain.model.user.Consulvisor;
import org.ecnusmartboys.infrastructure.data.mysql.table.ConsulvisorDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsulvisorConvertor {
    ConsulvisorDO toConsulvisor(Consulvisor consulvisor);

    List<Consulvisor> toConsulvisors(List<ConsulvisorDO> consulvisorDOs);
}
