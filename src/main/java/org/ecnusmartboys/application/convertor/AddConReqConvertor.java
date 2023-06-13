package org.ecnusmartboys.application.convertor;

import org.ecnusmartboys.application.dto.request.command.AddConsultantRequest;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.infrastructure.convertor.BaseConvertor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddConReqConvertor extends BaseConvertor<AddConsultantRequest, Consultant>{
    @Override
    @Mapping(target = "role", constant = Consultant.ROLE)
    Consultant toEntity(AddConsultantRequest dto);
}
