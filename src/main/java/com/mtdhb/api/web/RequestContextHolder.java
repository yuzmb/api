package com.mtdhb.api.web;

import org.springframework.core.NamedThreadLocal;

import com.mtdhb.api.dto.UserDTO;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/22
 */
public abstract class RequestContextHolder {

    private static final ThreadLocal<UserDTO> HOLDER = new NamedThreadLocal<UserDTO>("UserDTO");

    public static void reset() {
        HOLDER.remove();
    }

    public static void set(UserDTO value) {
        HOLDER.set(value);
    }

    public static UserDTO get() {
        return HOLDER.get();
    }

}
