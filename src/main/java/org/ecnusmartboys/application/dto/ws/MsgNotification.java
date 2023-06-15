package org.ecnusmartboys.application.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.MessageInfo;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MsgNotification {

    private String helpId;

    private String conversationId;

    private MessageInfo info;
}
