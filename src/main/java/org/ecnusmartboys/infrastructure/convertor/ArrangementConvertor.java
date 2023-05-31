package org.ecnusmartboys.infrastructure.convertor;

import org.ecnusmartboys.domain.model.arrangement.Arrangement;
import org.ecnusmartboys.infrastructure.data.mysql.table.ArrangementDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArrangementConvertor {
    List<Arrangement> toArrangements(List<ArrangementDO> arrangementDOS);
}
