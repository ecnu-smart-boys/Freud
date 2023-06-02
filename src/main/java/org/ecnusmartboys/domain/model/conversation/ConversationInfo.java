package org.ecnusmartboys.domain.model.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationInfo {

    private String conversationId;

    private Long startTime;

    private Long endTime;
}
