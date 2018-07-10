package com.mtdhb.api.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mtdhb.api.web.LoginInterceptor;

/**
 * @author i@huangdenghe.com
 * @date 2018/05/30
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns("/error", "/user/login",
                "/user/register", "/user/resetPassword", "/user/logout", "/user/registerMail", "/user/registerCaptcha",
                "/user/resetPasswordMail", "/user/resetPasswordCaptcha");
    }

}
