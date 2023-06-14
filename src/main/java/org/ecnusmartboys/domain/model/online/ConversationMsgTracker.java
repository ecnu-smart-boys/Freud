package org.ecnusmartboys.domain.model.online;

import lombok.Data;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class ConversationMsgTracker {

    private Lock lock;

    private long messageCount;

    private String conversationId;

    public ConversationMsgTracker(String conversationId) {
        messageCount = Long.parseLong(conversationId) << 32;
        lock = new ReentrantLock();
        this.conversationId = conversationId;
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
}
