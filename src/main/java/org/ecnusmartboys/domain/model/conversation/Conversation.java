package org.ecnusmartboys.domain.model.conversation;

import lombok.Getter;
import org.ecnusmartboys.domain.model.BaseEntity;
import org.ecnusmartboys.domain.model.user.User;

import java.util.Date;

public class Conversation extends BaseEntity {
    private User fromUser;
    private Comment fromUserComment;
    private User toUser;
    private Comment toUserComment;
    @Getter
    private Date startTime;
    @Getter
    private Date endTime;
    private Conversation helpConversation;
}
