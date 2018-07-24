package com.mtdhb.api.web;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mtdhb.api.dto.Result;
import com.mtdhb.api.exception.BusinessException;
import com.mtdhb.api.util.Results;

import lombok.extern.slf4j.Slf4j;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/06
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.warn(e.getSnapshot(), e);
        return Results.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn(e.getMessage(), e);
        return Results.error(HttpStatus.BAD_REQUEST.value(), "缺少参数", e.getParameterName());
    }

    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e) {
        log.warn(e.getMessage(), e);
        return Results.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
    }

    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return Results.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @ExceptionHandler(Exception.class)
    public Result exception(Exception e) {
        log.error(e.getMessage(), e);
        return Results.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

}
