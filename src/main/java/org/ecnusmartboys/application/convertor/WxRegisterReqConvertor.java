package org.ecnusmartboys.application.convertor;

import org.ecnusmartboys.application.dto.request.command.WxRegisterRequest;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.infrastructure.convertor.BaseConvertor;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WxRegisterReqConvertor extends BaseConvertor<WxRegisterRequest, Visitor> {
}
