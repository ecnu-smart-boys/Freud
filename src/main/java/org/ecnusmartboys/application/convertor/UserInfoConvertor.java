package org.ecnusmartboys.application.convertor;

import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.domain.model.user.User;
import org.ecnusmartboys.infrastructure.convertor.BaseConvertor;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserInfoConvertor extends BaseConvertor<UserInfo, User> {
}
