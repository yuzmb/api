package com.mtdhb.api.constant.e;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/07
 */
public enum HttpService {

    /**
     * 微信
     */
    WEIXIN,
    /**
     * QQ
     */
    QQ,
    /**
     * 微信小程序
     */
    MINI_PROGRAM;

    @JsonValue
    public int getJsonValue() {
        return ordinal();
    }

}
