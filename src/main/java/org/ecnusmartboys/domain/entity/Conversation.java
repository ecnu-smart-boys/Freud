package org.ecnusmartboys.domain.entity;

import lombok.Getter;
import org.ecnusmartboys.domain.value.Comment;

import java.util.Date;

public class Conversation extends BaseEntity{
    private String fromId;
    private User fromUser;
    private String toId;
    private User toUser;
    @Getter
    private Date startTime;
    @Getter
    private Date endTime;
    public User getFromUser() {
        return null;
    }

    public User getToUser() {
        return null;
    }

    /**
     * 获取访客评论
     */
    public Comment getFromComment(){
        return null;
    }

    /**
     * 获取咨询师评论
     */
    public Comment getToComment(){
        return null;
    }

    /**
     * 工厂类
     */
    public static class Factory {
        static public Conversation Retrieve(String conversationId){
            return new Conversation();
        }
        static public Conversation Retrieve(String fromId, String toId){
            return null;
        }
    }
}
