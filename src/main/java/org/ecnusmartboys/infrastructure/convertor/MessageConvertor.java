package org.ecnusmartboys.infrastructure.convertor;

import org.ecnusmartboys.domain.model.message.Message;
import org.ecnusmartboys.infrastructure.data.mysql.table.MessageDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageConvertor {

    MessageDO toMessageDO(Message message);

    Message toMessage(MessageDO messageDO);
}
