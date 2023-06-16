package org.ecnusmartboys.domain.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String id;

    private String msgKey;

    private long iterator;

    private String conversationId;

    private String fromId;

    private String toId;

    private String msgBody;

    private long time;

    private boolean revoked;


}
