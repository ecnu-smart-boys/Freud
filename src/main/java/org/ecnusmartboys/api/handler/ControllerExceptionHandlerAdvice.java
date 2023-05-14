package org.ecnusmartboys.api.handler;

import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.infrastructure.exception.*;
import org.ecnusmartboys.application.dto.response.Response;
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
    public Response<?> businessExceptionHandler(BusinessException exception){
        return Response.error(exception.getStatus(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<?> missingParamExceptionHandler(MissingServletRequestParameterException exception) {
        return Response.error(exception.getParameterName() + "不能为空");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Response<?> validExceptionHandler(BindException exception) {
        return Response.error(exception.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<?> validExceptionHandler(MethodArgumentNotValidException exception) {
        return Response.error(
                exception.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ResourceNotFoundException.class})
    public Response<?> resourceNotFoundExceptionHandler(Exception exception) {
        return Response.error(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            BadRequestException.class,
            IllegalArgumentException.class,
            MultipartException.class})
    public Response<?> badRequestExceptionHandler(Exception exception) {
        return Response.error(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalException.class)
    public Response<?> internalExceptionHandler(InternalException exception) {
        log.error("InternalException: ", exception);
        return Response.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务异常，请稍后重试");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public Response<?> forbiddenExceptionHandler(ForbiddenException exception) {
        return Response.error(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public Response<?> internalExceptionHandler(UnauthorizedException exception) {
        return Response.error(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response<?> methodNotAllowedExceptionHandler(HttpRequestMethodNotSupportedException exception) {
        return Response.error(HttpStatus.METHOD_NOT_ALLOWED.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Response<?> unhandledExceptionHandler(Exception exception) {
        log.error("Unhandled InternalException: ", exception);
        return Response.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务异常，请稍后重试");
    }

}
