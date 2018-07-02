package com.mtdhb.api.constant.e;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/14
 */
public enum CookieStatus {

    SUCCESS, USED, INVALID, LIMIT;

    @JsonValue
    public int getJsonValue() {
        return ordinal();
    }

}
