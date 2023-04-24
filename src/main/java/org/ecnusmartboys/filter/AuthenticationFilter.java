package org.ecnusmartboys.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.interceptor.AuthenticationInterceptor;
import org.ecnusmartboys.utils.SecurityUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * <p>对于用户每个请求都会经过这个过滤器，将用户认证信息存入当前线程环境。</p>
 * <p>用户信息在处理完请求后会在{@link AuthenticationInterceptor}</p>中移除。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = httpServletRequest.getSession();
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            SecurityUtil.setCurrentUserId(userId);
        }
        List<String> roles = (List<String>) session.getAttribute("roles");
        if (roles != null) {
            SecurityUtil.setCurrentUserRoles(roles);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
