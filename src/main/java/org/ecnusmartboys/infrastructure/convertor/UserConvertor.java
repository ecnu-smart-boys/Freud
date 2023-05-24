package org.ecnusmartboys.infrastructure.convertor;

import org.ecnusmartboys.domain.model.user.User;
import org.ecnusmartboys.infrastructure.data.mysql.RoleDO;
import org.ecnusmartboys.infrastructure.data.mysql.UserDO;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConvertor {
    @MapMapping
    User toUser(UserDO userDO, RoleDO roleDO);
}
