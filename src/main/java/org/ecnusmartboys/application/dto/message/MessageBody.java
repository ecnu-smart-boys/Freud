package org.ecnusmartboys.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageBody {

    private String MsgType;
    private Object MsgContent;
}
