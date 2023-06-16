package org.ecnusmartboys.adaptor.interceptor;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.adaptor.Extractor;
import org.ecnusmartboys.adaptor.annotation.AuthRoles;
import org.ecnusmartboys.infrastructure.mapper.UserMapper;
import org.jetbrains.annotations.NotNull;
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

    private final UserMapper userRepository;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod method) {
            if (!method.hasMethodAnnotation(AuthRoles.class)) {
                return true;
            }
            // 不加AnonymousAccess注解表示登录才可以访问
            var common = Extractor.extract(request);
            String userId = common.getUserId();
            if (userId == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            // 如果没有AnonymousAccess注解，必须登录
            var user = userRepository.selectById(userId);
            if (user == null || user.getDisabled()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            var authRoles = method.getMethodAnnotation(AuthRoles.class);
            if (authRoles != null && authRoles.value().length > 0) {
                String[] permissions = authRoles.value();
                if (!Arrays.asList(permissions).contains(user.getRole())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }
}
