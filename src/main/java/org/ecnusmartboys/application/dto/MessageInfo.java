package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageInfo {

    private String fromId;

    private String toId;

    private long iterator;

    private String msgBody;

    private String msgKey;

    private long time;

    private boolean revoked;
}
