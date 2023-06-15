package org.ecnusmartboys.domain.model.online;

import lombok.Data;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class ConversationMsgTracker {

    public static final long NULL_HELP = -1L;

    private Lock lock;

    private long messageCount;

    private String conversationId;

    private String helpId;

    private long supervisorId;


    public ConversationMsgTracker(String conversationId) {
        messageCount = Long.parseLong(conversationId) << 32;
        lock = new ReentrantLock();
        this.conversationId = conversationId;
        this.supervisorId = NULL_HELP;
    }

    public long increment() {
        long temp = 0;
        lock.lock();
        try {
            temp = messageCount;
            messageCount++;
        } finally {
            lock.unlock();
        }
        return temp;
    }

    public long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(long supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getHelpId() {
        return helpId;
    }

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }
}
