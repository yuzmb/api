package com.mtdhb.api.web;

import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mtdhb.api.constant.CustomHttpHeaders;
import com.mtdhb.api.constant.e.ErrorCode;
import com.mtdhb.api.dto.UserDTO;
import com.mtdhb.api.exception.BusinessException;
import com.mtdhb.api.service.UserService;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/03
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String userToken = request.getHeader(CustomHttpHeaders.X_USER_TOKEN);
        logger.info("method={}, uri={}, userToken={}", method, uri, userToken);
        if (userToken == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_EXCEPTION, "method={}, uri={}, userToken={}", method,
                    uri, userToken);
        }
        UserDTO userDTO = userService.getByToken(userToken);
        if (userDTO == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_EXCEPTION,
                    "method={}, uri={}, userToken={}, userDTO={}", method, uri, userToken, userDTO);

        }
        if (userDTO.getLocked()) {
            throw new BusinessException(ErrorCode.USER_LOCKED, "method={}, uri={}, userToken={}, userDTO={}", method,
                    uri, userToken, userDTO);
        }
        RequestContextHolder.set(userDTO);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        RequestContextHolder.reset();
    }

}
