package org.ecnusmartboys.api.interceptor;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements HandshakeInterceptor {

    private final RedisIndexedSessionRepository sessionRepository;

    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request,
                                   @NotNull ServerHttpResponse response,
                                   @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) throws Exception {
        var x = ((ServletServerHttpRequest) request).getServletRequest().getParameter("x-freud");
        if (x == null) {
            return false;
        }
        var session = (Session) sessionRepository.findById(x);
        if (session == null) {
            return false;
        }
        String userId = session.getAttribute("userId");
        attributes.put("userId", Long.valueOf(userId));

        return true;
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}
