package org.ecnusmartboys.infrastructure.mapper;

import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.infrastructure.model.mysql.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserInfoMapper extends BaseDTOMapper<UserInfo, User>{
}
