package org.ecnusmartboys.domain.model.conversation;

import lombok.Data;

@Data
public class Comment {
    private String id;

    private String conversationId;

    private String userId;

    private Integer score;

    private String text;

}
