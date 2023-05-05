package org.ecnusmartboys.interceptor;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.annotation.AnonymousAccess;
import org.ecnusmartboys.annotation.AuthRoles;
import org.ecnusmartboys.utils.SecurityUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 接口访问权限拦截器，用于权限校验。
 */
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            if (method.hasMethodAnnotation(AnonymousAccess.class)) {
                return true;
            }
            // 不加AnonymousAccess注解表示登录才可以访问
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            var authRoles = method.getMethodAnnotation(AuthRoles.class);
            if (authRoles != null && authRoles.value().length > 0) {
                String[] permissions = authRoles.value();
                var userRoles = SecurityUtil.getCurrentUserRoles();
                if (Arrays.stream(permissions).anyMatch(userRoles::contains)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        SecurityUtil.removeCurrentUser();
    }
}
