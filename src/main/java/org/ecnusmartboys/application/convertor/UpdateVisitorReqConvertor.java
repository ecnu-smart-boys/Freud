package org.ecnusmartboys.application.convertor;

import org.ecnusmartboys.application.dto.request.command.UpdateVisitorRequest;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.infrastructure.convertor.BaseConvertor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateVisitorReqConvertor extends BaseConvertor<UpdateVisitorRequest, Visitor> {
    @Override
    @Mapping(source = "nickName", target = "name")
    Visitor toEntity(UpdateVisitorRequest req);
}
