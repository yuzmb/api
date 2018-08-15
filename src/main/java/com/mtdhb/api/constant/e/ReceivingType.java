package com.mtdhb.api.constant.e;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author i@huangdenghe.com
 * @date 2018/08/14
 */
public enum ReceivingType {

    /**
     * 拼手气红包
     */
    LUCK,
    /**
     * 品质联盟专享红包
     */
    QUALITY;

    @JsonValue
    public int getJsonValue() {
        return ordinal();
    }

}
