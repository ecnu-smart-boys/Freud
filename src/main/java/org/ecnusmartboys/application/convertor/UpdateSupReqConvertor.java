package org.ecnusmartboys.application.convertor;

import org.ecnusmartboys.application.dto.request.command.UpdateSupervisorRequest;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.infrastructure.convertor.BaseConvertor;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateSupReqConvertor extends BaseConvertor<UpdateSupervisorRequest, Supervisor>{
}
