package com.mtdhb.api.constant;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author i@huangdenghe.com
 * @date 2018/05/31
 */
public enum ReceivingStatus {

    ING, SUCCESS, FAILURE;

    @JsonValue
    public int getJsonValue() {
        return ordinal();
    }

}
