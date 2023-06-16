package org.ecnusmartboys.adaptor.handler;

import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.infrastructure.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

/**
 * 全局异常处理，在service, controller层抛出的未处理的异常都会由这个类处理。
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandlerAdvice {
    /**
     * 发生业务异常，如搜索用户ID未找到等，可能发生但符合预期的异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public Responses<?> businessExceptionHandler(BusinessException exception) {
        return Responses.error(exception.getStatus(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Responses<?> missingParamExceptionHandler(MissingServletRequestParameterException exception) {
        return Responses.error(exception.getParameterName() + "不能为空");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Responses<?> validExceptionHandler(BindException exception) {
        return Responses.error(exception.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Responses<?> validExceptionHandler(MethodArgumentNotValidException exception) {
        return Responses.error(
                exception.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ResourceNotFoundException.class})
    public Responses<?> resourceNotFoundExceptionHandler(Exception exception) {
        return Responses.error(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            BadRequestException.class,
            IllegalArgumentException.class,
            MultipartException.class})
    public Responses<?> badRequestExceptionHandler(Exception exception) {
        return Responses.error(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalException.class)
    public Responses<?> internalExceptionHandler(InternalException exception) {
        log.error("InternalException: ", exception);
        return Responses.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务异常，请稍后重试");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public Responses<?> forbiddenExceptionHandler(ForbiddenException exception) {
        return Responses.error(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public Responses<?> internalExceptionHandler(UnauthorizedException exception) {
        return Responses.error(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Responses<?> methodNotAllowedExceptionHandler(HttpRequestMethodNotSupportedException exception) {
        return Responses.error(HttpStatus.METHOD_NOT_ALLOWED.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Responses<?> unhandledExceptionHandler(Exception exception) {
        log.error("Unhandled InternalException: ", exception);
        return Responses.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务异常，请稍后重试");
    }

}
