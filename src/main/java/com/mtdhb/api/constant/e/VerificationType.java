package com.mtdhb.api.constant.e;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author i@huangdenghe.com
 * @date 2017/12/12
 */
public enum VerificationType {

    /**
     * 邮箱
     */
    MAIL,
    /**
     * 手机
     */
    PHONE;

    @JsonValue
    public int getJsonValue() {
        return ordinal();
    }

}
