package org.ecnusmartboys.infrastructure.exception;

/**
 * 用户登录认证失败或者<b>未登录</b>但是访问了需要登录才能访问的资源时抛出。
 */
public class UnauthorizedException extends RuntimeException {
    public static UnauthorizedException AUTHENTICATION_FAIL = new UnauthorizedException("认证失败");
    public static UnauthorizedException ANONYMOUS_NOT_ALLOWED = new UnauthorizedException("禁止匿名访问");
    public UnauthorizedException(String message) {
        super(message);
    }
}
