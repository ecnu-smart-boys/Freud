package org.ecnusmartboys.domain.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String id;

    private String msgKey;

    private String conversationId;

    private String fromId;

    private String toId;

    private String msgBody;

    private long time;

    private boolean revoked;


}
