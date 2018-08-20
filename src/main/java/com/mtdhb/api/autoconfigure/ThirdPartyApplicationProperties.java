package com.mtdhb.api.autoconfigure;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/19
 */
@Component
@ConfigurationProperties(prefix = "com.mtdhb.api.third-party-application")
@Data
public class ThirdPartyApplicationProperties {

    /**
     * 每个链接的红包个数
     */
    private int[] totals;
    /**
     * 手气最佳红包的最小金额
     */
    private BigDecimal[] minimums;
    /**
     * 每人每天可以领红包的次数
     */
    private long[] dailies;
    /**
     * 提取链接的唯一标识的正则表达式
     */
    private Pattern[] patterns;

}
