package com.mtdhb.api.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/06/01
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.mtdhb.api.nodejs")
public class NodejsConfiguration {

    /**
     * Node.js 服务的 URL
     */
    private String url;
    /**
     * 校验 cookie 的接口路径
     */
    private String checkCookie;
    /**
     * 领取红包的接口路径
     */
    private String getHongbao;

}
