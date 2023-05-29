package org.ecnusmartboys.api.interceptor;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.api.annotation.AnonymousAccess;
import org.jetbrains.annotations.NotNull;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接口访问权限拦截器，用于权限校验。
 */
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final RedisIndexedSessionRepository sessionRepository;


    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            if (method.hasMethodAnnotation(AnonymousAccess.class)) {
                return true;
            }
            // 不加AnonymousAccess注解表示登录才可以访问
            // TODO
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

    }
}
