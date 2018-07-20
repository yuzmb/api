package com.mtdhb.api.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mtdhb.api.web.LoginInterceptor;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/20
 */
@Configuration
@EnableConfigurationProperties(LoginInterceptorProperties.class)
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    private LoginInterceptor loginInterceptor;
    private LoginInterceptorProperties loginInterceptorProperties;

    WebMvcAutoConfiguration(LoginInterceptor loginInterceptor, LoginInterceptorProperties loginInterceptorProperties) {
        this.loginInterceptor = loginInterceptor;
        this.loginInterceptorProperties = loginInterceptorProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println(this.loginInterceptorProperties);
        registry.addInterceptor(this.loginInterceptor)
                .addPathPatterns(this.loginInterceptorProperties.getIncludePatterns())
                .excludePathPatterns(this.loginInterceptorProperties.getExcludePatterns());
    }

}
