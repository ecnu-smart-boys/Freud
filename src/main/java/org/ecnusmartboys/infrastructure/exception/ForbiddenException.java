package org.ecnusmartboys.infrastructure.exception;

/**
 * 用户已登录，但是权限不足时抛出此异常。
 */
public class ForbiddenException extends RuntimeException {
    public static final ForbiddenException DISABLED = new ForbiddenException("账号已被禁用");
    public static final ForbiddenException PERMISSION_DENY = new ForbiddenException("账号权限不足");
    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
