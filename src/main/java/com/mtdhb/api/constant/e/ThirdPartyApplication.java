package com.mtdhb.api.constant.e;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author i@huangdenghe.com
 * @date 2018/03/07
 */
public enum ThirdPartyApplication {

    /**
     * 美团
     */
    MEITUAN,
    /**
     * 饿了么
     */
    ELE;

    @JsonValue
    public int getJsonValue() {
        return ordinal();
    }

}
