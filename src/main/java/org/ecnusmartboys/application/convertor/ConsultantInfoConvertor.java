package org.ecnusmartboys.application.convertor;

import org.ecnusmartboys.application.dto.ConsultantInfo;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.infrastructure.convertor.BaseConvertor;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsultantInfoConvertor extends BaseConvertor<ConsultantInfo, Consultant> {
}
