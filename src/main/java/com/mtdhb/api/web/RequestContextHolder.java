package com.mtdhb.api.web;

import org.springframework.core.NamedThreadLocal;

import com.mtdhb.api.dto.UserDTO;

/**
 * @author i@huangdenghe.com
 * @date 2018/04/22
 */
public abstract class RequestContextHolder {

    private static final ThreadLocal<UserDTO> holder = new NamedThreadLocal<UserDTO>("UserDTO");

    public static void reset() {
        holder.remove();
    }

    public static void set(UserDTO value) {
        holder.set(value);
    }

    public static UserDTO get() {
        return holder.get();
    }

}
