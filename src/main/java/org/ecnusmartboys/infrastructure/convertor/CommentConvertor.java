package org.ecnusmartboys.infrastructure.convertor;

import org.ecnusmartboys.domain.model.conversation.Comment;
import org.ecnusmartboys.infrastructure.data.mysql.table.CommentDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentConvertor {
    Comment toComment(CommentDO commentDO);
}
