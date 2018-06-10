package com.mtdhb.api.web;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mtdhb.api.dto.Result;
import com.mtdhb.api.exception.BusinessException;
import com.mtdhb.api.util.Results;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/06
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        logger.warn(e.getSnapshot(), e);
        return Results.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.warn(e.getMessage(), e);
        return Results.error(HttpStatus.BAD_REQUEST.value(), "缺少参数", e.getParameterName());
    }

    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e) {
        logger.warn(e.getMessage(), e);
        List<FieldError> fieldErrors = e.getFieldErrors();
        Map<String, String> data = new HashMap<>(fieldErrors.size());
        Iterator<FieldError> iterator = fieldErrors.iterator();
        while (iterator.hasNext()) {
            FieldError fieldError = iterator.next();
            data.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return Results.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), data);
    }

    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        logger.error(e.getMessage(), e);
        return Results.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @ExceptionHandler(Exception.class)
    public Result exception(Exception e) {
        logger.error(e.getMessage(), e);
        return Results.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

}
