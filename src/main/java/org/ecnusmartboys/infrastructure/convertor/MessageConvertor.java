package org.ecnusmartboys.infrastructure.convertor;

import org.ecnusmartboys.domain.model.message.Message;
import org.ecnusmartboys.infrastructure.data.mysql.table.MessageDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Date;
import java.util.List;

@Mapper(componentModel = BaseConvertor.COMPONENT_MODEL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageConvertor {

    MessageDO toMessageDO(Message message);

    Message toMessage(MessageDO messageDO);

    List<Message> toMessages(List<MessageDO> dOs);

    default Date mapToTime(long value) {
        return new Date(value);
    }

    default long mapFromTime(Date value) {
        return value.getTime();
    }
}
