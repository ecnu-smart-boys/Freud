package org.ecnusmartboys.application.convertor;

import org.ecnusmartboys.application.dto.request.command.AddSupervisorRequest;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.infrastructure.convertor.BaseConvertor;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddSupReqConvertor extends BaseConvertor<AddSupervisorRequest, Supervisor>{
}
