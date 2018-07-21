package com.mtdhb.api.autoconfigure;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author i@huangdenghe.com
 * @date 2018/07/19
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.mtdhb.api.third-party-application")
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
    private long[] availables;

}
