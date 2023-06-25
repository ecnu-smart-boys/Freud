package org.ecnusmartboys.adaptor.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.dto.enums.OnlineState;
import org.ecnusmartboys.application.service.OnlineStateService;
import org.ecnusmartboys.application.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketServer extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final OnlineStateService onlineStateService;
    private final UserService userService;
    @Value("${freud.online.timeout:15}")
    private int timeout;

    public boolean send(Long userId, String message) {
        WebSocketSession session = sessionMap.get(userId);
        return send(session, message);
    }

    public void close(Long userId) {
        WebSocketSession session = sessionMap.get(userId);
        if (session != null) {
            try {
                session.close();
                sessionMap.remove(userId);
            } catch (Exception e) {
                log.error("Failed to close session for userId {}", userId, e);
            }
        }
    }

    private boolean send(WebSocketSession session, String message) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                return true;
            } catch (Exception e) {
                log.error("Failed to send message to sessionId {}", session.getId(), e);
            }
        }
        return false;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) {
            log.debug("WebSocket connection closed because of no user id");
            session.close();
            return;
        }
        log.debug("WebSocket connection established for userId {}", userId);

        sessionMap.put(userId, session);
        session.sendMessage(new TextMessage("🤗" + userId));

        onlineStateService.setUserState(userId, OnlineState.IDLE);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        onlineStateService.refreshTimeout(userId, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        log.debug("WebSocket connection closed for userId {}", userId);

//        sessionMap.remove(userId);

        userService.offline(userId);
    }


    public void notifyUser(Long userId, String content) {
        WebSocketSession session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                send(userId, content);
            } catch (Exception e) {
                log.error("Failed to close session for userId {}", userId, e);
            }
        }
    }
}
