package mapstruct;

import org.ecnusmartboys.infrastructure.mapper.BaseDTOMapper;
import org.ecnusmartboys.infrastructure.model.mysql.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NestedFieldMapper extends BaseDTOMapper<NestedUser, User> {

    @Override
    @Mapping(source = ".", target = "user")
    NestedUser toDto(User entity);

    @Override
    @Mapping(source = "user", target = ".")
    User toEntity(NestedUser dto);
}
