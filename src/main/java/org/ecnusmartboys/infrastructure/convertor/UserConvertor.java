package org.ecnusmartboys.infrastructure.convertor;

import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.domain.model.user.User;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.infrastructure.data.mysql.RoleDO;
import org.ecnusmartboys.infrastructure.data.mysql.StaffInfoDO;
import org.ecnusmartboys.infrastructure.data.mysql.UserDO;
import org.ecnusmartboys.infrastructure.data.mysql.VisitorInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConvertor {
    User toUser(UserDO userDO, RoleDO roleDO);
    Visitor toVisitor(UserDO userDO, RoleDO roleDO, VisitorInfoDO visitorInfoDO);
    Consultant toConsultant(UserDO userDO, RoleDO roleDO, StaffInfoDO staffInfoDO);
    Supervisor toSupervisor(UserDO userDO, RoleDO roleDO, StaffInfoDO staffInfoDO);
    UserDO toUserDO(User user);
    RoleDO toRoleDO(User user);
    VisitorInfoDO toVisitorInfoDO(Visitor visitor);
    StaffInfoDO toStaffInfoDO(Consultant consultant);
}
