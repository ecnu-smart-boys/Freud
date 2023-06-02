package org.ecnusmartboys.domain.model.conversation;

import lombok.Data;
import org.ecnusmartboys.domain.model.BaseEntity;
import org.ecnusmartboys.domain.model.user.User;


@Data
public class Conversation extends BaseEntity {
    private User fromUser;
    private Comment fromUserComment;
    private User toUser;
    private Comment toUserComment;
    private Long startTime;
    private Long endTime;
    private Help helper;
}
