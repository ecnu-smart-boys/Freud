package org.ecnusmartboys.infrastructure.utils;

import java.util.List;

/**
 * 保存当前线程的用户信息
 */
public class SecurityUtil {

    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    private static final ThreadLocal<List<String>> CURRENT_USER_ROLES = new ThreadLocal<>();

    public static Long getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    public static void setCurrentUserId(Long id) {
        CURRENT_USER_ID.set(id);
    }

    public static List<String> getCurrentUserRoles() {
        return CURRENT_USER_ROLES.get();
    }

    public static void setCurrentUserRoles(List<String> roles) {
        CURRENT_USER_ROLES.set(roles);
    }

    public static void removeCurrentUser() {
        CURRENT_USER_ID.remove();
        CURRENT_USER_ROLES.remove();
    }

    public static boolean isLogin() {
        return CURRENT_USER_ID.get() != null;
    }
}
