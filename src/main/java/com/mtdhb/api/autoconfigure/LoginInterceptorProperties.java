package com.mtdhb.api.autoconfigure;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/20
 */
@Data
@ConfigurationProperties(prefix = "com.mtdhb.api.login-interceptor")
public class LoginInterceptorProperties {

    private List<String> includePatterns;

    private List<String> excludePatterns;

}
