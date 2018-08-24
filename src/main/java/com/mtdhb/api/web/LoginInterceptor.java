package com.mtdhb.api.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mtdhb.api.constant.CustomHttpHeaders;
import com.mtdhb.api.constant.e.ErrorCode;
import com.mtdhb.api.dto.UserDTO;
import com.mtdhb.api.exception.BusinessException;
import com.mtdhb.api.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/03
 */
@Component
@Slf4j
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String remoteAddr = request.getRemoteAddr();
        String forwardedFor = request.getHeader(CustomHttpHeaders.X_FORWARDED_FOR);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String userToken = request.getHeader(CustomHttpHeaders.X_USER_TOKEN);
        log.info("remoteAddr={}, forwardedFor={}, method={}, uri={}, userToken={}", remoteAddr, forwardedFor, method,
                uri, userToken);
        if (userToken == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_EXCEPTION,
                    "remoteAddr={}, forwardedFor={}, method={}, uri={}, userToken={}", remoteAddr, forwardedFor, method,
                    uri, userToken);
        }
        UserDTO userDTO = userService.getByToken(userToken);
        if (userDTO == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_EXCEPTION,
                    "remoteAddr={}, forwardedFor={}, method={}, uri={}, userToken={}, userDTO={}", remoteAddr,
                    forwardedFor, method, uri, userToken, userDTO);
        }
        if (userDTO.getLocked()) {
            throw new BusinessException(ErrorCode.USER_LOCKED,
                    "remoteAddr={}, forwardedFor={}, method={}, uri={}, userToken={}, userDTO={}", remoteAddr,
                    forwardedFor, method, uri, userToken, userDTO);
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
